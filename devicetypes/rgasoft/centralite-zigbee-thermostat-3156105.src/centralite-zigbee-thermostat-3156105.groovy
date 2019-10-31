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
metadata {
	definition (name: "Centralite Zigbee Thermostat 3156105", namespace: "RGASoft", author: "Richard Gareau", cstHandler: true) {
		capability "Temperature Measurement"
		capability "Thermostat Heating Setpoint"
        capability "Thermostat Cooling Setpoint"
        capability "Thermostat Fan Mode"
        capability "Thermostat Mode"
        capability "Thermostat Operating State"
        capability "Configuration"
        capability "Health Check"
        capability "Refresh"
        
        fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0201, 0204", outClusters: "0402", manufacturer: "Centralite", model: "3156105", deviceJoinName: "Centralite Thermostat 3156105"
	}


	simulator {
		// TODO: define status and reply messages here
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
        standardTile("refresh", "device.refresh", decoration: "flat", width: 2, height: 2) {
			state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
        standardTile("configure", "device.configure", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "configure", label:'', action:"configuration.configure", icon:"st.secondary.configure"
		}
	}
    
    main("temperature")
    details(["temperature", "heatingSetpoint", "coolingSetpoint", "configure", "refresh"])
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing --- Start"
	
    log.debug "parse got called with '${description}'"
    
    // TODO: handle 'temperature' attribute
    
	// TODO: handle 'heatingSetpoint' attribute
	
    log.debug "Parsing --- Done"
}

// handle commands
/*** Thermostat Heating Setpoint ***/
def setHeatingSetpoint(Double degree) {
	log.debug "setHeatingSetpoint --- Start"
    log.debug "Setting heat set point to " + degree
    if (degree != null) {
		def temperatureScale = getTemperatureScale()
		float minSetpoint = thermostatSetpointRange[minSetpointIndex]
		float maxSetpoint = thermostatSetpointRange[maxSetpointIndex]

		if (preciseDegrees >= minSetpoint && preciseDegrees <= maxSetpoint) {
			def degrees = new BigDecimal(preciseDegrees).setScale(1, BigDecimal.ROUND_HALF_UP)
			def celsius = (getTemperatureScale() == "C") ? degrees : (fahrenheitToCelsius(degrees) as Float).round(2)

			log.debug "setHeatingSetpoint({$degrees} ${temperatureScale})"

			zigbee.writeAttribute(THERMOSTAT_CLUSTER, ATTRIBUTE_HEAT_SETPOINT, DataType.INT16, zigbee.convertToHexString(celsius * 100, 4)) +
				zigbee.readAttribute(THERMOSTAT_CLUSTER, ATTRIBUTE_HEAT_SETPOINT) +
				zigbee.readAttribute(THERMOSTAT_CLUSTER, ATTRIBUTE_PI_HEATING_STATE)
		} else {
			log.debug "heatingSetpoint $preciseDegrees out of range! (supported: $minSetpoint - $maxSetpoint ${getTemperatureScale()})"
		}
	}
    
    log.debug "setHeatingSetpoint --- Done"
}
/*** Thermostat Heating Setpoint ***/

/*** Thermostat Cooling Setpoint ***/
def setCoolingSetpoint(Double coolSetPoint) {
	log.debug "setCoolingSetpoint --- Start"
	
    log.debug "Setting cooling set point to " + coolSetPoint
    // TODO: handle 'setCoolingSetpoint' command
    
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
def auto()
{
	log.debug "auto --- Start"
    setThermostatMode("auto")
    log.debug "auto --- Done"
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

def heat()
{
	log.debug "heat --- Start"
    setThermostatMode("heat")
    log.debug "heat --- Done"
}

def off()
{
	log.debug "off --- Start"
    setThermostatMode("off")
    log.debug "off --- Done"
}

def setThermostatMode(String mode)
{
	log.debug "setThermostatMode --- Start"
    log.debug "setThermostatMode '${mode}'"
    log.debug "setThermostatMode --- Done"
}
/*** Capability Thermostat Mode ***/


/*** Capability Refresh ***/
def refresh()
{
	log.debug "refresh --- Start"

	log.debug "refresh --- Done"
}
/*** Capability Refresh ***/

/*** Capability Configure ***/
def configure() {
	log.debug "configure --- Start"

	log.debug "configure --- Done"
}
/*** Capability Configure ***/

/*** Capability Health ***/
def ping() {
	log.debug "ping --- Start"
    
    log.debug "ping --- Done"
}
/*** Capability Health ***/