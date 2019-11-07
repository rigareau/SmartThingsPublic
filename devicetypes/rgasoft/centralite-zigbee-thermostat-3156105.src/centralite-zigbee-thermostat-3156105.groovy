/**
 *  Centralite Zigbee Thermostat 3156105
 *
 *  Copyright 2019 Richard Gareau
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 */
 
import physicalgraph.zigbee.zcl.DataType

metadata {
	definition (name: "Centralite Zigbee Thermostat 3156105", namespace: "RGASoft", author: "Richard Gareau", cstHandler: true) {
		capability "Battery"
        capability "Temperature Measurement"
		capability "Thermostat Heating Setpoint"
        capability "Thermostat Cooling Setpoint"
        capability "Thermostat Fan Mode"
        capability "Thermostat Mode"
        capability "Thermostat Operating State"
        capability "Power Source"
        capability "Configuration"
        capability "Health Check"
        capability "Refresh"
        capability "Actuator"
        capability "Sensor"
        
        fingerprint profileId: "0104", inClusters: "0000, 0001, 0201, 0202, 0204", outClusters: "0402", manufacturer: "Centralite", model: "3156105", deviceJoinName: "Centralite Thermostat 3156105"
	}


	simulator {
		// TODO: define status and reply messages here
        /***
        log.debug "Simulator method called"
        
        status "system mode off":  "read attr - raw: 7AF81902010A0000293408, dni: 7AF8, endpoint: 19, cluster: 0201, size: 10, attrId: 001C, encoding: 20, value: 00"
        status "system mode auto": "read attr - raw: 7AF81902010A0000293408, dni: 7AF8, endpoint: 19, cluster: 0201, size: 10, attrId: 001C, encoding: 20, value: 01"
        status "system mode cool": "read attr - raw: 7AF81902010A0000293408, dni: 7AF8, endpoint: 19, cluster: 0201, size: 10, attrId: 001C, encoding: 20, value: 03"
        status "system mode heat": "read attr - raw: 7AF81902010A0000293408, dni: 7AF8, endpoint: 19, cluster: 0201, size: 10, attrId: 001C, encoding: 20, value: 04"
		status "catchall": "catchall: 0104 0201 01 01 0000 00 A6BA 01 01 106A 22 01 00000101"
        
        reply "system mode off": "catchall: 0104 0201 01 01 0000 00 A6BA 01 01 106A 22 01 01000000"
        ***/
	}

	tiles {
    	valueTile("temperature", "device.temperature", width: 2, height: 2) {
			state("temperature", label:'${currentValue}°', unit:"F",
				backgroundColors:[
					[value: 31, color: "#153591"],
					[value: 44, color: "#1e9cbb"],
					[value: 59, color: "#90d2a7"],
					[value: 74, color: "#44b621"],
					[value: 84, color: "#f1d801"],
					[value: 95, color: "#d04e00"],
					[value: 96, color: "#bc2323"]
				]
			)
		}
     
        valueTile("heatingSetpoint", "device.heatingSetpoint", width: 2, height: 2) {
			state "heatingSetpoint", label:'Setpoint ${currentValue}°', backgroundColors:[
					// Celsius
					[value: 0, color: "#153591"],
					[value: 7, color: "#1e9cbb"],
					[value: 15, color: "#90d2a7"],
					[value: 23, color: "#44b621"],
					[value: 28, color: "#f1d801"],
					[value: 35, color: "#d04e00"],
					[value: 37, color: "#bc2323"],
					// Fahrenheit
					[value: 40, color: "#153591"],
					[value: 44, color: "#1e9cbb"],
					[value: 59, color: "#90d2a7"],
					[value: 74, color: "#44b621"],
					[value: 84, color: "#f1d801"],
					[value: 95, color: "#d04e00"],
					[value: 96, color: "#bc2323"]
				]
		}
        
        valueTile("coolingSetpoint", "device.coolingSetpoint", width: 2, height: 2) {
			state "coolingSetpoint", label:'Setpoint ${currentValue}°', backgroundColors:[
					// Celsius
					[value: 0, color: "#153591"],
					[value: 7, color: "#1e9cbb"],
					[value: 15, color: "#90d2a7"],
					[value: 23, color: "#44b621"],
					[value: 28, color: "#f1d801"],
					[value: 35, color: "#d04e00"],
					[value: 37, color: "#bc2323"],
					// Fahrenheit
					[value: 40, color: "#153591"],
					[value: 44, color: "#1e9cbb"],
					[value: 59, color: "#90d2a7"],
					[value: 74, color: "#44b621"],
					[value: 84, color: "#f1d801"],
					[value: 95, color: "#d04e00"],
					[value: 96, color: "#bc2323"]
				]
		}
        
        controlTile("thermostatFanMode", "device.thermostatFanMode", "enum", width: 2 , height: 2, supportedStates: "device.supportedThermostatFanModes") {
			state "auto", action: "setThermostatFanMode", label: 'Auto', icon: "st.thermostat.fan-auto"
			state "on",	action: "setThermostatFanMode", label: 'On', icon: "st.thermostat.fan-on"
		}
        
        standardTile("thermostatmode", "device.thermostatMode", inactiveLabel: false, decoration: "flat") {
			state "off", label: "off", action:"thermostat.off", icon:"st.thermostat.heating-cooling-off"
			state "cool", label: "cool", action:"thermostat.cool", icon:"st.thermostat.cool"
			state "heat", label: "heat", action:"thermostat.heat", icon:"st.thermostat.heat"
			state "auto", label: "auto", action:"thermostat.auto", icon:"st.thermostat.auto"
		}
        
        standardTile("thermostatOperatingState", "device.thermostatOperatingState", inactiveLabel: false, decoration: "flat") {
			state "idle", label: "Idle", icon:"st.thermostat.heating-cooling-off"
			state "cooling", label: "Cooling", icon:"st.thermostat.cool"
			state "heating", label: "Heating", icon:"st.thermostat.heat"
			state "fan only", label: "Fan Only", icon:"st.thermostat.auto"
		}
        
        valueTile("battery", "device.battery", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "battery", label:'${currentValue}% battery', unit:""
		}
                
        valueTile("powerSource", "device.powerSource", width: 2, heigh: 1, inactiveLabel: true, decoration: "flat") {
			state "powerSource", label: 'Power Source: ${currentValue}', action:"configuration.configure", backgroundColor: "#ffffff"
		}
        
        standardTile("refresh", "device.refresh", decoration: "flat", width: 2, height: 2) {
			state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
        standardTile("configure", "device.configure", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "configure", label:'', action:"configuration.configure", icon:"st.secondary.configure"
		}
	}
    
    main("temperature")
    details(["temperature", "heatingSetpoint", "coolingSetpoint", "thermostatMode", "battery", "powerSource", "configure", "refresh"])
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
    
    def map = [:]
    
    def descMap = zigbee.parseDescriptionAsMap(description)
    log.debug "Desc Map: $descMap"
    if (descMap.clusterInt == THERMOSTAT_CLUSTER) {
        log.debug "We are in Thermostat Cluster"
		if (descMap.attrInt == ATTRIBUTE_LOCAL_TEMPERATURE) {
        	log.debug "TEMP"
			map.name = "temperature"
			map.value = getTemperature(descMap.value)
			map.unit = getTemperatureScale()
        }

        if (descMap.attrInt == ATTRIBUTE_SYSTEM_MODE) {
            log.debug "MODE - ${descMap.value}"
            def value = modeMap[descMap.value]
            map.name = "thermostatMode"
            map.value = value
        }
        
        if (descMap.attrInt == ATTRIBUTE_COOLING_SETPOINT) {
            log.debug "COOLING SETPOINT"
			map.name = "coolingSetpoint"
			map.value = getTemperature(descMap.value)
			map.unit = getTemperatureScale()
        }
        
        if (descMap.attrInt == ATTRIBUTE_HEATING_SETPOINT) {
            log.debug "HEATING SETPOINT"
			map.name = "heatingSetpoint"
			map.value = getTemperature(descMap.value)
			map.unit = getTemperatureScale()
        }
        
        if (descMap.attrInt == ATTRIBUTE_RUNNING_STATE) {
        	log.debug "RUNNING STATE"
            def intValue = zigbee.convertHexToInt(descMap.value)
            /**
			 * Zigbee Cluster Library spec 6.3.2.2.3.7
			 * Bit	Description
			 *  0	Heat State
			 *  1	Cool State
			 *  2	Fan State
			 *  3	Heat 2nd Stage State
			 *  4	Cool 2nd Stage State
			 *  5	Fan 2nd Stage State
			 *  6	Fan 3rd Stage Stage
			 **/
            map.name = "thermostatOperatingState"
            if (intValue & 0x01) {
                map.value = "heating"
            } else if (intValue & 0x02) {
                map.value = "cooling"
            } else if (intValue & 0x04) {
                map.value = "fan only"
            } else {
                map.value = "idle"
            }
        }
    }
	
    if (descMap.clusterInt == FAN_CONTROL_CLUSTER) {
        log.debug "Fan Control Cluster"
		if (descMap.attrInt == ATTRIBUTE_FAN_MODE) {
        	log.debug "FAN MODE"
			map.name = "thermostatFanMode"
			map.value = ATTRIBUTE_FAN_MODE_MAP[descMap.value]
			map.data = [supportedThermostatFanModes: state.supportedFanModes]
        }
    }
    

    if (descMap.clusterInt == zigbee.POWER_CONFIGURATION_CLUSTER) {
    	log.debug "Power Configuration Cluster"
        switch (descMap.attrInt) {
        	case ATTRIBUTE_MAINS_INFORMATION:
            	log.debug "MAINS INFORMATION ${descMap.data}"
                break
            case ATTRIBUTE_MAINS_SETTINGS:
            	log.debug "MAINS SETTINGS ${descMap.data}"
                break
        	case ATTRIBUTE_BATTERY_INFORMATION:
            	log.debug "BATTERY INFORMATION ${descMap.data}"
                break
            case ATTRIBUTE_BATTERY_SETTINGS:
            	log.debug "BATTERY SETTINGS ${descMap.data}"
                break
        	case ATTRIBUTE_BATTERY_VOLTAGE:
            	log.debug "BATTERY VOLTAGE ${descMap.value}"
                break
            case ATTRIBUTE_BATTERY_PERCENTAGE:
            	log.debug "BATTERY PERCENTAGE ${descMap.data}"
                map.name = "battery"
                map.value = 50
                break
            case ATTRIBUTE_BATTERY_ALARM_STATE:
            	log.debug "BATTERY ALARM STATE ${descMap.data}"
            default:
            	log.debug "Attribute: ${descMap.attrInt} Value: ${descMap.value}"
        }
    }

    if (descMap.clusterInt == BASIC_CLUSTER && descMap.attrInt == ATTRIBUTE_POWER_SOURCE) {
        def powerSource = getPowerSourceMap(descMap.value)
        log.debug "POWER SOURCE is : ${powerSource}"
        map.name = "powerSource"
        map.value = powerSource
    } 
        
    def result = null
	if (map) {
		result = createEvent(map)
	}
	log.debug "Parse returned $result"
	return result
}

// handle commands
/*** Thermostat Heating Setpoint ***/
def setHeatingSetpoint(Double preciseDegrees) {
	log.debug "setHeatingSetpoint(${preciseDegrees}) --- Start"
    
    def retVal = ""
    def currentValue = device.currentValue("heatingSetpoint")
    
    if (preciseDegrees != null) {
		def temperatureScale = getTemperatureScale()
		float minSetpoint = thermostatSetpointRange["min"]
		float maxSetpoint = thermostatSetpointRange["max"]

		if (preciseDegrees >= minSetpoint && preciseDegrees <= maxSetpoint) {
			def degrees = new BigDecimal(preciseDegrees).setScale(1, BigDecimal.ROUND_HALF_UP)
			def celsius = (getTemperatureScale() == "C") ? degrees : (fahrenheitToCelsius(degrees) as Float).round(2)

			log.debug "setHeatingSetpoint(${degrees} ${temperatureScale})"

			sendEvent("name":"heatingSetpoint", "value":degrees)
			retVal = zigbee.writeAttribute(THERMOSTAT_CLUSTER, ATTRIBUTE_HEATING_SETPOINT, DataType.INT16, zigbee.convertToHexString(celsius * 100, 4))
		} else {
        	
			log.debug "heatingSetpoint $preciseDegrees out of range! (supported: $minSetpoint - $maxSetpoint ${getTemperatureScale()})"
            sendEvent("name":"heatingSetpoint", "value":currentValue)
            retVal = zigbee.readAttribute(THERMOSTAT_CLUSTER, ATTRIBUTE_HEATING_SETPOINT)
		}
	}
    return retVal
    log.debug "setHeatingSetpoint --- Done"
}
/*** Thermostat Heating Setpoint ***/

/*** Thermostat Cooling Setpoint ***/
def setCoolingSetpoint(Double preciseDegrees) {
	log.debug "setCoolingSetpoint(${preciseDegrees}) --- Start"
	
    def retVal = ""
    def currentValue = device.currentValue("coolingSetpoint")
    
    if (preciseDegrees != null) {
		def temperatureScale = getTemperatureScale()
		float minSetpoint = thermostatSetpointRange["min"]
		float maxSetpoint = thermostatSetpointRange["max"]

		if (preciseDegrees >= minSetpoint && preciseDegrees <= maxSetpoint) {
			def degrees = new BigDecimal(preciseDegrees).setScale(1, BigDecimal.ROUND_HALF_UP)
			def celsius = (getTemperatureScale() == "C") ? degrees : (fahrenheitToCelsius(degrees) as Float).round(2)

			log.debug "setCoolingSetpoint(${degrees} ${temperatureScale})"

			sendEvent("name":"coolingSetpoint", "value":degrees)
			retVal = zigbee.writeAttribute(THERMOSTAT_CLUSTER, ATTRIBUTE_COOLING_SETPOINT, DataType.INT16, zigbee.convertToHexString(celsius * 100, 4))
		} else {
			log.debug "coolingSetpoint $preciseDegrees out of range! (supported: $minSetpoint - $maxSetpoint ${getTemperatureScale()})"
            sendEvent("name":"coolingSetpoint", "value":currentValue)
            retVal = zigbee.readAttribute(THERMOSTAT_CLUSTER, ATTRIBUTE_COOLING_SETPOINT)
		}
	}
    return retVal
    log.debug "setCoolingSetpoint --- Done"
}
/*** Thermostat Cooling Setpoint ***/

/*** Capability Thermostat Fan Mode ***/
def fanAuto(){
	log.debug "fanAuto --- Start"
    setThermostatFanMode("auto")
    log.debug "fanAuto --- Done"
}

def fanCirculate(){
	log.debug "fanCirculate --- Start"
    setThermostatFanMode("circulate")
    log.debug "fanCirculate --- Done"
}

def fanOn(){
	log.debug "fanOn --- Start"
    setThermostatFanMode("on")
    log.debug "fanOn --- Done"
}

def setThermostatFanMode(String mode){
	log.debug "setThermostatFanMode --- Start"
    
    log.debug "Received mode: " + mode
    
    log.debug "setThermostatFanMode --- Done"
}
/*** Capability Thermostat Fan Mode ***/

/*** Capability Thermostat Mode ***/
def heat()
{
	log.debug "heat --- Start"
    setThermostatMode("heat")
    log.debug "heat --- Done"
}

def cool()
{
	log.debug "cool --- Start"
    setThermostatMode("cool")
    log.debug "cool --- Done"
}

def emergencyHeat()
{
	log.debug "emergencyHeat --- Start"
    setThermostatMode("emergencyheat")
    log.debug "emergencyHeat --- Done"
}

def auto()
{
	log.debug "auto --- Start"
    setThermostatMode("auto")
    log.debug "auto --- Done"
}

def off()
{
	log.debug "off --- Start"
    setThermostatMode("off")
    log.debug "off --- Done"
}

def setThermostatMode(String mode)
{
	log.debug "setThermostatMode --- Start ${mode}"
    def retval = ""
    if (supportedThermostatModes.contains(mode)) {
		int modeNumber;
        switch(mode) {
            case "heat":
				modeNumber = 04
                break
            case "cool":
                modeNumber = 03
                break
            case "auto":
                modeNumber = 01
                break
            case "off":
				modeNumber = 00
                break
        }
        
        sendEvent(name:"thermostatMode",value:"${mode}")
       	retval = zigbee.writeAttribute(THERMOSTAT_CLUSTER, ATTRIBUTE_SYSTEM_MODE, DataType.ENUM8, modeNumber)
        
    } else {
    	log.debug "'${mode}' is not supported"
    }    
    log.debug "setThermostatMode --- return $retVal"
    return retval
}
/*** Capability Thermostat Mode ***/


/*** Capability Refresh ***/
def refresh()
{
	log.debug "refresh"
    return zigbee.readAttribute(THERMOSTAT_CLUSTER, ATTRIBUTE_LOCAL_TEMPERATURE) +
		zigbee.readAttribute(THERMOSTAT_CLUSTER, ATTRIBUTE_COOLING_SETPOINT) +
		zigbee.readAttribute(THERMOSTAT_CLUSTER, ATTRIBUTE_HEATING_SETPOINT) +
		zigbee.readAttribute(THERMOSTAT_CLUSTER, ATTRIBUTE_SYSTEM_MODE) +
        zigbee.readAttribute(THERMOSTAT_CLUSTER, ATTRIBUTE_RUNNING_STATE) +
        zigbee.readAttribute(FAN_CONTROL_CLUSTER, ATTRIBUTE_FAN_MODE) +
        zigbee.readAttribute(zigbee.POWER_CONFIGURATION_CLUSTER, ATTRIBUTE_BATTERY_VOLTAGE) +
        zigbee.readAttribute(BASIC_CLUSTER, ATTRIBUTE_POWER_SOURCE)
}
/*** Capability Refresh ***/

/*** Capability Configure ***/
def configure() {
	log.debug "configure --- Start"
    def bindings = zigbee.addBinding(THERMOSTAT_CLUSTER) +
    	zigbee.addBinding(FAN_CONTROL_CLUSTER) +
        zigbee.addBinding(BASIC_CLUSTER) +
        zigbee.addBinding(POWER_CONFIGURATION_CLUSTER)
        
	def startValues = zigbee.writeAttribute(THERMOSTAT_CLUSTER, ATTRIBUTE_HEATING_SETPOINT, DataType.INT16, 0x07D0) +
		zigbee.writeAttribute(THERMOSTAT_CLUSTER, ATTRIBUTE_COOLING_SETPOINT, DataType.INT16, 0x0A28)
                    
        // Thermostat Current Temperature every 5 minutes up to an hour change amount 1
    def reporting = zigbee.configureReporting(THERMOSTAT_CLUSTER, ATTRIBUTE_LOCAL_TEMPERATURE, DataType.INT16, 0, 0, 0x01) + 
        // Thermostat Operating State report to send whenever it changes (no min or max, or change threshold).
    	zigbee.configureReporting(THERMOSTAT_CLUSTER, ATTRIBUTE_RUNNING_STATE, DataType.BITMAP16, 0, 0, null) +  
        // Thermostat Mode report to send whenever it changes (no min or max, or change threshold).
        zigbee.configureReporting(THERMOSTAT_CLUSTER, ATTRIBUTE_SYSTEM_MODE, DataType.ENUM8, 0, 0, null) +
        zigbee.configureReporting(POWER_CONFIGURATION_CLUSTER, ATTRIBUTE_BATTERY_PERCENTAGE, DataType.UINT8, 0, 30, null) +
        zigbee.configureReporting(POWER_CONFIGURATION_CLUSTER, ATTRIBUTE_BATTERY_VOLTAGE, DataType.UINT8, 0, 30, null) +
        zigbee.configureReporting(THERMOSTAT_CLUSTER, ATTRIBUTE_HEATING_SETPOINT, DataType.INT16, 0, 0, 0x01) +
        zigbee.configureReporting(THERMOSTAT_CLUSTER, ATTRIBUTE_COOLING_SETPOINT, DataType.INT16, 0, 0, 0x01)
    
    log.debug "bindings: ${bindings}"
    log.debug "startvalues: ${startValues}"
    log.debug "reporting: ${reporting}"
	log.debug "configure --- done"
    return bindings + startValues + reporting + zigbee.batteryConfig() + refresh()
}
/*** Capability Configure ***/

/*** Capability Health ***/
def ping() {
	log.debug "ping --- Start"
    
    log.debug "ping --- Done"
}
/*** Capability Health ***/

def getTemperature(value) {
	if (value != null) {
		def celsius = Integer.parseInt(value, 16) / 100
		if (getTemperatureScale() == "C") {
			return celsius
		} else {
			return celsiusToFahrenheit(celsius)
		}
	}
}

private getBatteryLevel(int intValue) {
	def min = 2.1
    def max = 3.3
    def vBatt = intValue / 10
    return ((vBatt - min) / (max - min) * 100)
}

private String getPowerSourceMap(value) {
	log.debug "getPowerSourceMap(${value})"
	String retVal = ""
    if (value == "81") {
        retVal = "mains"
    } else {
        retVal = "batteries"
    }
    return retVal
}

def getSupportedThermostatModes() {
	["heat", "cool", "auto", "off"]
}

def getModeMap() {[
	"00":"off",
    "01":"auto",
    "03":"cool",
	"04":"heat"
]}

private getThermostatSetpointRange() {
	[
      "min":07,
      "max":30
    ]
}
private getBASIC_CLUSTER() { 0x0000 }
private getATTRIBUTE_POWER_SOURCE() { 0x0007 }

private getTHERMOSTAT_CLUSTER() { 0x0201 }
private getATTRIBUTE_LOCAL_TEMPERATURE() { 0x0000 }
private getATTRIBUTE_COOLING_SETPOINT() { 0x0011 }
private getATTRIBUTE_HEATING_SETPOINT() { 0x0012 }
private getATTRIBUTE_SYSTEM_MODE() { 0x001C }
private getATTRIBUTE_RUNNING_STATE() { 0x0029 }

private getFAN_CONTROL_CLUSTER() { 0x0202 }
private getATTRIBUTE_FAN_MODE() { 0x0000 }
private getATTRIBUTE_FAN_MODE_MAP() {
	[
		"04":"on",
		"05":"auto"
	]
}

private getPOWER_CONFIGURATION_CLUSTER() { 0X0001 }
private getATTRIBUTE_MAINS_INFORMATION() { 0x0000 }
private getATTRIBUTE_MAINS_SETTINGS() { 0x0000 }
private getATTRIBUTE_BATTERY_INFORMATION() { 0x002 }
private getATTRIBUTE_BATTERY_SETTINGS() { 0x003 }
private getATTRIBUTE_BATTERY_VOLTAGE() { 0x0020 }
private getATTRIBUTE_BATTERY_PERCENTAGE() { 0x0021 }
private getATTRIBUTE_BATTERY_ALARM_STATE() { 0x003E }