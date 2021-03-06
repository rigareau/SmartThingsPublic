/**
 *  SmartSense Temp/Humidity Sensor
 *
 *  Copyright 2014 SmartThings
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
 */
import physicalgraph.zigbee.zcl.DataType

metadata {
	definition(name: "Sonoff Temp/Humidity Sensor", namespace: "RGASoft", author: "Richard Gareau", runLocally: true, executeCommandsLocally: false, ocfDeviceType: "oic.d.thermostat") {
		capability "Configuration"
		capability "Battery"
		capability "Refresh"
		capability "Temperature Measurement"
		capability "Relative Humidity Measurement"
		capability "Health Check"
		capability "Sensor"

		fingerprint profileId: "0104", inClusters: "0000,0001,0003,0402,0405", outClusters: "0003", manufacturer: "eWeLink", model: "TH01", deviceJoinName: "Sonoff Multipurpose Sensor"
	}

	simulator {
		status 'H 40': 'catchall: 0104 FC45 01 01 0140 00 D9B9 00 04 C2DF 0A 01 000021780F'
		status 'H 45': 'catchall: 0104 FC45 01 01 0140 00 D9B9 00 04 C2DF 0A 01 0000218911'
		status 'H 57': 'catchall: 0104 FC45 01 01 0140 00 4E55 00 04 C2DF 0A 01 0000211316'
		status 'H 53': 'catchall: 0104 FC45 01 01 0140 00 20CD 00 04 C2DF 0A 01 0000219814'
		status 'H 43': 'read attr - raw: BF7601FC450C00000021A410, dni: BF76, endpoint: 01, cluster: FC45, size: 0C, attrId: 0000, result: success, encoding: 21, value: 10a4'
	}

	preferences {
		input "tempOffset", "number", title: "Temperature offset", description: "Select how many degrees to adjust the temperature.", range: "-100..100", displayDuringSetup: false
		input "humidityOffset", "number", title: "Humidity offset", description: "Enter a percentage to adjust the humidity.", range: "*..*", displayDuringSetup: false
	}

	tiles(scale: 2) {
		multiAttributeTile(name: "temperature", type: "generic", width: 6, height: 4, canChangeIcon: true) {
			tileAttribute("device.temperature", key: "PRIMARY_CONTROL") {
				attributeState "temperature", label: '${currentValue}°',
						backgroundColors: [
								[value: 31, color: "#153591"],
								[value: 44, color: "#1e9cbb"],
								[value: 59, color: "#90d2a7"],
								[value: 74, color: "#44b621"],
								[value: 84, color: "#f1d801"],
								[value: 95, color: "#d04e00"],
								[value: 96, color: "#bc2323"]
						]
			}
		}
		valueTile("humidity", "device.humidity", inactiveLabel: false, width: 2, height: 2) {
			state "humidity", label: '${currentValue}% humidity', unit: ""
		}
		valueTile("battery", "device.battery", decoration: "flat", inactiveLabel: false, width: 2, height: 2) {
			state "battery", label: '${currentValue}% battery'
		}
		standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", action: "refresh.refresh", icon: "st.secondary.refresh"
		}

		main "temperature", "humidity"
		details(["temperature", "humidity", "battery", "refresh"])
	}
}

def parse(String description) {
	log.debug "Parsing '${description}'"
    
	// getEvent will handle temperature and humidity
	Map map = zigbee.getEvent(description)
    log.debug "Map: ${map}"
	if (!map) {
		Map descMap = zigbee.parseDescriptionAsMap(description)
        log.debug("descMap: ${descMap}")
		if (descMap.clusterInt == zigbee.POWER_CONFIGURATION_CLUSTER) {
			if (descMap.attrInt == 0x0020) {
				map = getBatteryResult(Integer.parseInt(descMap.value, 16))
			} else if (descMap.attrInt == 0x0021) {
				map = getBatteryPercentageResult(Integer.parseInt(descMap.value,16))
			} else if (descMap.commandInt == 7) {
            	if (descMap.data[0] == "00") {
            		log.debug("Battery reporting config response: success")
                } else {
                	log.warn("Battery reporting config failed with error: ${descMap.data[0]}")
                }
            } else {
            	log.warn("Unexpected event: ${descMap}")
            }
		} else if (descMap.clusterInt == zigbee.TEMPERATURE_MEASUREMENT_CLUSTER) {
			if (descMap.commandInt == 7) {
            	if (descMap.data[0] == "00") {
					log.debug "Temp reporting config response: success"
                } else {
					log.warn "Temperature reporting config failed with error: ${descMap.data[0]}"
				}
			} else {
            	log.warn("Unexpected event: ${descMap}")
            }
		} else if (descMap.clusterInt == zigbee.RELATIVE_HUMIDITY_CLUSTER) {
			if (descMap.commandInt == 7) {
            	if (descMap.data[0] == "00") {
					log.debug "Relative humidity reporting config response: success"
                } else {
					log.warn "Relative humidity reporting config failed with error: ${descMap.data[0]}"
				}
			} else {
            	log.warn("Unexpected event: ${descMap}")
            }
		} 
	} else if (map.name == "temperature") {
		if (tempOffset) {
			map.value = new BigDecimal((map.value as float) + (tempOffset as float)).setScale(1, BigDecimal.ROUND_HALF_UP)
		}
		map.descriptionText = temperatureScale == 'C' ? '{{ device.displayName }} was {{ value }}°C' : '{{ device.displayName }} was {{ value }}°F'
		map.translatable = true
	} else if (map.name == "humidity") {
		if (humidityOffset) {
			map.value = (int) map.value + (int) humidityOffset
		}
	}

	log.debug "Parse returned $map"
	return map ? createEvent(map) : [:]
}


private Map getBatteryPercentageResult(rawValue) {
	log.debug "Battery Percentage rawValue = ${rawValue} -> ${rawValue / 2}%"
	def result = [:]

	if (0 <= rawValue && rawValue <= 200) {
		result.name = 'battery'
		result.translatable = true
		result.value = Math.round(rawValue / 2)
		result.descriptionText = "${device.displayName} battery was ${result.value}%"
	}

	return result
}

private Map getBatteryResult(rawValue) {
	log.debug 'Battery'
	def linkText = getLinkText(device)

	def result = [:]

	def volts = rawValue / 10
	if (!(rawValue == 0 || rawValue == 255)) {
		def minVolts = 2.1
		def maxVolts = 3.0
		def pct = (volts - minVolts) / (maxVolts - minVolts)
		def roundedPct = Math.round(pct * 100)
		if (roundedPct <= 0)
			roundedPct = 1
		result.value = Math.min(100, roundedPct)
		result.descriptionText = "${linkText} battery was ${result.value}%"
		result.name = 'battery'

	}

	return result
}

/**
 * PING is used by Device-Watch in attempt to reach the Device
 * */
def ping() {
	log.debug("Ping")
	return zigbee.readAttribute(zigbee.POWER_CONFIGURATION_CLUSTER, 0x0020) // Read the Battery Level
}

def installed() {
// Device wakes up every 1 hour, this interval allows us to miss one wakeup notification before marking offline
	log.debug "Configured health checkInterval when installed()"
	sendEvent(name: "checkInterval", value: 2 * 60 * 60 + 2 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
}

def updated() {
// Device wakes up every 1 hours, this interval allows us to miss one wakeup notification before marking offline
	log.debug "Configured health checkInterval when updated()"
	sendEvent(name: "checkInterval", value: 2 * 60 * 60 + 2 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
}

def refresh() {
	log.debug "refresh temperature, humidity, and battery"
	def reads = zigbee.readAttribute(zigbee.POWER_CONFIGURATION_CLUSTER, 0x0020) +
    			zigbee.readAttribute(zigbee.TEMPERATURE_MEASUREMENT_CLUSTER, 0x0000)+
				zigbee.readAttribute(zigbee.RELATIVE_HUMIDITY_CLUSTER, 0x0000)
    return reads
}

def configure() {
	log.debug "Configuring Reporting and Bindings."
	
	def reporting = zigbee.configureReporting(zigbee.POWER_CONFIGURATION_CLUSTER, 0x0020, DataType.UINT8, 30, 21600, 0x01) +
    				zigbee.configureReporting(zigbee.RELATIVE_HUMIDITY_CLUSTER, 0x0000, DataType.UINT16, 60, 600, 1*100) +
					zigbee.configureReporting(zigbee.TEMPERATURE_MEASUREMENT_CLUSTER, 0x0000, DataType.INT16, 60, 600, 0xA)
	return reporting
}