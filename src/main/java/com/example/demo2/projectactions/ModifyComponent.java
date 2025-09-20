package com.example.demo2.projectactions;

import com.example.demo2.Project;
import com.example.demo2.componentmodel.BatteryModel;
import com.example.demo2.componentmodel.Component;
import com.example.demo2.componentmodel.LightbulbModel;
import com.example.demo2.componentmodel.ResistorModel;
import com.example.demo2.db.ConnDbOps;

public class ModifyComponent implements ProjectActions {
    private final Project PROJECT;
    private final Component COMPONENT;
    private final double INITIAL_VALUE;
    private final double NEW_VALUE;

    public ModifyComponent(Project currentProject, Component component, double initialValue, double newValue) {
        PROJECT = currentProject;
        COMPONENT = component;
        INITIAL_VALUE = initialValue;
        NEW_VALUE = newValue;
    }

    @Override
    public void performAction() {
        switch (COMPONENT.getComponentType()) {
            case "Battery" -> {
                BatteryModel batteryModel = (BatteryModel) COMPONENT;
                ConnDbOps.updateComponents(COMPONENT, NEW_VALUE);
                batteryModel.setVoltage(NEW_VALUE);
                PROJECT.calculateCircuitGroups();
            }
            case "Resistor" -> {
                ResistorModel resistorModel = (ResistorModel) COMPONENT;
                ConnDbOps.updateComponents(COMPONENT, NEW_VALUE);
                resistorModel.setResistance(NEW_VALUE);
                PROJECT.calculateCircuitGroups();
            }
            case "Light bulb" -> {
                LightbulbModel lightbulbModel = (LightbulbModel) COMPONENT;
                ConnDbOps.updateComponents(COMPONENT, NEW_VALUE);
                lightbulbModel.setResistance(NEW_VALUE);
                PROJECT.calculateCircuitGroups();
            }
        }
        PROJECT.addToUndoStack(this);
    }

    @Override
    public void undo() {
        switch (COMPONENT.getComponentType()) {
            case "Battery" -> {
                BatteryModel batteryModel = (BatteryModel) COMPONENT;
                ConnDbOps.updateComponents(COMPONENT, INITIAL_VALUE);
                batteryModel.setVoltage(INITIAL_VALUE);
                PROJECT.calculateCircuitGroups();
            }
            case "Resistor" -> {
                ResistorModel resistorModel = (ResistorModel) COMPONENT;
                ConnDbOps.updateComponents(COMPONENT, INITIAL_VALUE);
                resistorModel.setResistance(INITIAL_VALUE);
                PROJECT.calculateCircuitGroups();
            }
            case "Light bulb" -> {
                LightbulbModel lightbulbModel = (LightbulbModel) COMPONENT;
                ConnDbOps.updateComponents(COMPONENT, INITIAL_VALUE);
                lightbulbModel.setResistance(INITIAL_VALUE);
                PROJECT.calculateCircuitGroups();
            }
        }
        PROJECT.addToRedoStack(this);
    }

    @Override
    public void redo() {
        this.performAction();
    }
}
