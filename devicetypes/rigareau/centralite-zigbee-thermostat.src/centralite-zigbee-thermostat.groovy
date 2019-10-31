/**
 *  Copyright 2018 SmartThings
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *	CentraLite Thermostat
 *
 *	Author: RIGASoft
 *	Date: 2019-10-28
 */

import groovy.json.JsonOutput
import physicalgraph.zigbee.zcl.DataType

metadata {
    definition (name: "Centralite Zigbee Thermostat", namespace: "rigareau", author: "Richard Gareau", mnmn: "SmartThings", vid: "generic-thermostat-1", genericHandler: "Zigbee") {
        capability "Temperature Measurement"

        fingerprint profileId: "0104", inClusters: "0000,0001,0003,0004,0005,0020,0201,0202,0204,0B05", outClusters: "000A, 0019",  manufacturer: "LUX", model: "KONOZ", deviceJoinName: "LUX KONOz Thermostat"
        fingerprint profileId: "0104", manufacturer: "CentraLite Systems", model: "3156105", deviceJoinName: "CentraLite Thermostat 3156105"
    }

    tiles {}
}




def parse(String description) {
    log.debug "Parse description $description"

    def map = zigbee.getEvent(description)
    def result

    if (!map) {
        result = parseAttrMessage(description)
    } else {
        log.warn "Unexpected event: ${map}"
    }

    log.debug "Description ${description} parsed to ${result}"

    return result
}

private parseAttrMessage(description) {
    def descMap = zigbee.parseDescriptionAsMap(description)
    def result = []
    List attrData = [[cluster: descMap.clusterInt, attribute: descMap.attrInt, value: descMap.value]]

    log.debug "Desc Map: $descMap"

    descMap.additionalAttrs.each {
        attrData << [cluster: descMap.clusterInt, attribute: it.attrInt, value: it.value]
    }
    attrData.each {
        def map = [:]
        if (it.cluster == THERMOSTAT_CLUSTER) {
            if (it.attribute == ATTRIBUTE_LOCAL_TEMPERATURE) {
                log.debug "TEMP"
                map.name = "temperature"
                map.value = getTemperature(it.value)
                map.unit = temperatureScale
            }
        } else {
        	log.debug "Unknow attribute " + it.attribute
        }

        if (map) {
            result << createEvent(map)
        }
    }

    return result
}

def installed() {
	log.debug "Processing installed --- Start"
    
    log.debug "Processing installed --- Done"
}

def refresh() {
    log.debug "Processing refresh --- Start"
    
    log.debug "Processing refresh --- Done"
}

def ping() {
	log.debug "Processing ping --- Start"
    refresh()
    log.debug "Processing ping --- Done"
}

def configure() {
	log.debug "Processing configure --- Start"
    def requests = []
    log.debug "binding to Thermostat cluster and Fan Cluster"

    requests += zigbee.addBinding(THERMOSTAT_CLUSTER) + zigbee.addBinding(FAN_CONTROL_CLUSTER)

// Configure Thermostat Cluster
    log.debug "Configure reporting for Thermostat Cluster " + THERMOSTAT_CLUSTER + ", ATTRIBUTE_LOCAL_TEMP " + ATTRIBUTE_LOCAL_TEMPERATURE
    requests += zigbee.configureReporting(THERMOSTAT_CLUSTER, ATTRIBUTE_LOCAL_TEMPERATURE, DataType.INT16, 10, 60, 50)

    log.debug "Configure reporting for Thermostat Cluster " + THERMOSTAT_CLUSER + ", ATTRIBUTE_HEATING_SETPOINT " + ATTRIBUTE_HEATING_SETPOINT
    requests += zigbee.configureReporting(THERMOSTAT_CLUSTER, ATTRIBUTE_HEATING_SETPOINT, DataType.INT16, 1, 600, 50)

    log.debug "Configure reporting for Thermostat Cluster " + THERMOSTAT_CLUSER + ", Attribute Local Temp " + ATTRIBUTE_THERMOSTAT_MODE
    requests += zigbee.configureReporting(THERMOSTAT_CLUSTER, ATTRIBUTE_THERMOSTAT_MODE, DataType.ENUM8, 1, 0, 1)

    //requests += zigbee.configureReporting(THERMOSTAT_CLUSTER, ATTRIBUTE_MFR_SPEC_SETPOINT_MODE, DataType.ENUM8, 1, 0, 1)
    requests += zigbee.configureReporting(THERMOSTAT_CLUSTER, ATTRIBUTE_PI_HEATING_STATE, DataType.UINT8, 1, 600, 1)

    // Configure Thermostat Ui Conf Cluster
    //requests += zigbee.configureReporting(THERMOSTAT_UI_CONFIG_CLUSTER, ATTRIBUTE_TEMP_DISP_MODE, DataType.ENUM8, 1, 0, 1)
    //requests += zigbee.configureReporting(THERMOSTAT_UI_CONFIG_CLUSTER, ATTRIBUTE_KEYPAD_LOCKOUT, DataType.ENUM8, 1, 0, 1)

    // Read the configured variables
    log.debug "Reading Thermostat cluster: " + THERMOSTAT_CLUSTER + ", ATTRIBUTE_LOCAL_TEMP: " + ATTRIBUTE_LOCAL_TEMPERATURE
    requests += zigbee.readAttribute(THERMOSTAT_CLUSTER, ATTRIBUTE_LOCAL_TEMPERATURE)

    log.debug "Reading Thermostat cluster: " + THERMOSTAT_CLUSTER + ", ATTRIBUTE_HEATING_SETPOINT: " + ATTRIBUTE_HEATING_SETPOINT
    requests += zigbee.readAttribute(THERMOSTAT_CLUSTER, ATTRIBUTE_HEATING_SETPOINT)

    log.debug "Reading Thermostat cluster: " + THERMOSTAT_CLUSTER + ", ATTRIBUTE_THERMOSTAT_MODE: " + ATTRIBUTE_THERMOSTAT_MODE
    requests += zigbee.readAttribute(THERMOSTAT_CLUSTER, ATTRIBUTE_THERMOSTAT_MODE)

    //requests += zigbee.readAttribute(THERMOSTAT_CLUSTER, ATTRIBUTE_MFR_SPEC_SETPOINT_MODE, ["mfgCode": "0x1185"])
    requests += zigbee.readAttribute(THERMOSTAT_CLUSTER, ATTRIBUTE_PI_HEATING_STATE)
    //requests += zigbee.readAttribute(THERMOSTAT_UI_CONFIG_CLUSTER, ATTRIBUTE_TEMP_DISP_MODE)
    //requests += zigbee.readAttribute(THERMOSTAT_UI_CONFIG_CLUSTER, ATTRIBUTE_KEYPAD_LOCKOUT)

    log.debug "Processing configure --- Done"
    return requests
}

def getTemperature(value) {
    log.debug "Processing configure --- Start"
    if (value != null) {
        def celsius = Integer.parseInt(value, 16) / 100
        if (temperatureScale == "C") {
            retVal = Math.round(celsius)
        } else {
            retVal = Math.round(celsiusToFahrenheit(celsius))
        }
    }
    log.debug "Processing configure --- Done"
    return retVal
}


private hex(value) {
    return new BigInteger(Math.round(value).toString()).toString(16)
}

private hexToInt(value) {
    log.debug "hexToInt $value"
    if (value != null) {
        return new BigInteger(value, 16)
    } else {
        return 0
    }
}

// TODO: Get these from the thermostat; for now they are set to match the UI metadata
def getCoolingSetpointRange() {
    (getTemperatureScale() == "C") ? [10, 35] : [50, 95]
}
def getHeatingSetpointRange() {
    (getTemperatureScale() == "C") ? [7.22, 32.22] : [45, 90]
}



private getTHERMOSTAT_CLUSTER() { 0x0201 }
private getATTRIBUTE_LOCAL_TEMPERATURE() { 0x0000 }
private getATTRIBUTE_PI_HEATING_STATE() { 0x0008 }
private getATTRIBUTE_THERMOSTAT_SYSTEM_CONFIG() { 0x0009 } // Optional attribute
private getATTRIBUTE_COOLING_SETPOINT() { 0x0011 }
private getATTRIBUTE_HEATING_SETPOINT() { 0x0012 }
private getATTRIBUTE_THERMOSTAT_RUNNING_MODE() { 0x001E }
private getATTRIBUTE_CONTROL_SEQUENCE_OF_OPERATION() { 0x001B } // Mandatory attribute
private getATTRIBUTE_CONTROL_SEQUENCE_OF_OPERATION_MAP() {
    [
            "00":["off", "cool"],
            "01":["off", "cool"],
            // 0x02, 0x03, 0x04, and 0x05 don't actually guarentee emergency heat; to learn this, one would
            // try THERMOSTAT_SYSTEM_CONFIG (optional), which we default to for the LUX KONOz since it supports THERMOSTAT_SYSTEM_CONFIG
            "02":["off", "heat", "emergency heat"],
            "03":["off", "heat", "emergency heat"],
            "04":["off", "heat", "auto", "cool", "emergency heat"],
            "05":["off", "heat", "auto", "cool", "emergency heat"]
    ]
}
private getATTRIBUTE_THERMOSTAT_MODE() { 0x001C }
private getATTRIBUTE_THERMOSTAT_MODE_OFF() { 0x00 }
private getATTRIBUTE_THERMOSTAT_MODE_AUTO() { 0x01 }
private getATTRIBUTE_THERMOSTAT_MODE_COOL() { 0x03 }
private getATTRIBUTE_THERMOSTAT_MODE_HEAT() { 0x04 }
private getATTRIBUTE_THERMOSTAT_MODE_EMERGENCY_HEAT() { 0x05 }
private getATTRIBUTE_THERMOSTAT_MODE_MAP() {
    [
            "00":"off",
            "01":"auto",
            "03":"cool",
            "04":"heat",
            "05":"emergency heat"
    ]
}
private getATTRIBUTE_THERMOSTAT_RUNNING_STATE() { 0x0029 }
private getSETPOINT_RAISE_LOWER_CMD() { 0x00 }

private getFAN_CONTROL_CLUSTER() { 0x0202 }
private getATTRIBUTE_FAN_MODE() { 0x0000 }
private getATTRIBUTE_FAN_MODE_SEQUENCE() { 0x0001 }
private getATTRIBUTE_FAN_MODE_SEQUENCE_MAP() {
    [
            "00":["low", "medium", "high"],
            "01":["low", "high"],
            "02":["low", "medium", "high", "auto"],
            "03":["low", "high", "auto"],
            "04":["on", "auto"],
    ]
}
private getATTRIBUTE_FAN_MODE_ON() { 0x04 }
private getATTRIBUTE_FAN_MODE_AUTO() { 0x05 }
private getATTRIBUTE_FAN_MODE_MAP() {
    [
            "04":"on",
            "05":"auto"
    ]
}

private getBATTERY_VOLTAGE() { 0x0020 }
private getBATTERY_ALARM_STATE() { 0x003E }