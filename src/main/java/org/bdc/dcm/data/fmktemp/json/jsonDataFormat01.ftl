<#if parentMac?? || 
     powerStatus?? || 
     settingTemperature?? || 
     senseTemperature?? || 
     settingWindpower?? || 
     settingMode?? || 
     senseElectricity?? || 
     senseVoltage?? || 
     residueElectroTime?? || 
     residueEnergy?? || 
     allElectroTime?? || 
     allEnergy?? || 
     power?? || 
     pcos?? || 
     networkStatus?? || 
     msglist?? || 
     jobs??>
{
    "mac": [
    <#list maclist as mac>
        "${mac!}"<#if mac_index lt maclist?size-1>,</#if>
    </#list>
    ],
    "property": {
        <#if parentMac??>"parent_mac": "${parentMac}"
             <#if powerStatus?? ||
                  settingTemperature?? || 
                  senseTemperature?? || 
                  settingWindpower?? || 
                  settingMode?? || 
                  senseElectricity?? || 
                  senseVoltage?? || 
                  residueElectroTime?? || 
                  residueEnergy?? || 
                  allElectroTime?? || 
                  allEnergy?? || 
                  power?? || 
                  pcos?? || 
                  networkStatus?? || 
                  msglist?? || 
                  jobs??>
                  ,
             </#if>
        </#if>
        <#if powerStatus??>"power_status": "<#if 1==powerStatus?number>on<#else>off</#if>"
             <#if settingTemperature?? || 
                  senseTemperature?? || 
                  settingWindpower?? || 
                  settingMode?? || 
                  senseElectricity?? || 
                  senseVoltage?? || 
                  residueElectroTime?? || 
                  residueEnergy?? || 
                  allElectroTime?? || 
                  allEnergy?? || 
                  power?? || 
                  pcos?? || 
                  networkStatus?? || 
                  msglist?? || 
                  jobs??>
                  ,
             </#if>
        </#if>
        <#if settingTemperature??>"setting_temperature": "${settingTemperature}"
             <#if settingTemperature?? || 
                  settingWindpower?? || 
                  settingMode?? || 
                  senseElectricity?? || 
                  senseVoltage?? || 
                  residueElectroTime?? || 
                  residueEnergy?? || 
                  allElectroTime?? || 
                  allEnergy?? || 
                  power?? || 
                  pcos?? || 
                  networkStatus?? || 
                  msglist?? || 
                  jobs??>
                  ,
             </#if>
        </#if>
        <#if senseTemperature??>"sense_temperature": "${senseTemperature}"
             <#if settingWindpower?? || 
                  settingMode?? || 
                  senseElectricity?? || 
                  senseVoltage?? || 
                  residueElectroTime?? || 
                  residueEnergy?? || 
                  allElectroTime?? || 
                  allEnergy?? || 
                  power?? || 
                  pcos?? || 
                  networkStatus?? || 
                  msglist?? || 
                  jobs??>
                  ,
             </#if>
        </#if>
        <#if settingWindpower??>"setting_windpower": "${settingWindpower}"
             <#if settingMode?? || 
                  senseElectricity?? || 
                  senseVoltage?? || 
                  residueElectroTime?? || 
                  residueEnergy?? || 
                  allElectroTime?? || 
                  allEnergy?? || 
                  power?? || 
                  pcos?? || 
                  networkStatus?? || 
                  msglist?? || 
                  jobs??>
                  ,
             </#if>
        </#if>
        <#if settingMode??>"setting_mode": "${settingMode}"
             <#if senseElectricity?? || 
                  senseVoltage?? || 
                  residueElectroTime?? || 
                  residueEnergy?? || 
                  allElectroTime?? || 
                  allEnergy?? || 
                  power?? || 
                  pcos?? || 
                  networkStatus?? || 
                  msglist?? || 
                  jobs??>
                  ,
             </#if>
        </#if>
        <#if senseElectricity??>"sense_electricity": "${senseElectricity}mA"
             <#if senseVoltage?? || 
                  residueElectroTime?? || 
                  residueEnergy?? || 
                  allElectroTime?? || 
                  allEnergy?? || 
                  power?? || 
                  pcos?? || 
                  networkStatus?? || 
                  msglist?? || 
                  jobs??>
                  ,
             </#if>
        </#if>
        <#if senseVoltage??>"sense_voltage": "${senseVoltage}V"
             <#if residueElectroTime?? || 
                  residueEnergy?? || 
                  allElectroTime?? || 
                  allEnergy?? || 
                  power?? || 
                  pcos?? || 
                  networkStatus?? || 
                  msglist?? || 
                  jobs??>
                  ,
             </#if>
        </#if>
        <#if residueElectroTime??>"residue_electro_time": "${residueElectroTime}h"
             <#if residueEnergy?? || 
                  allElectroTime?? || 
                  allEnergy?? || 
                  power?? || 
                  pcos?? || 
                  networkStatus?? || 
                  msglist?? || 
                  jobs??>
                  ,
             </#if>
        </#if>
        <#if residueEnergy??>"residue_energy": "${residueEnergy}kW/h"
             <#if allElectroTime?? || 
                  allEnergy?? || 
                  power?? || 
                  pcos?? || 
                  networkStatus?? || 
                  msglist?? || 
                  jobs??>
                  ,
             </#if>
        </#if>
        <#if allElectroTime??>"all_electro_time": "${allElectroTime}h"
             <#if allEnergy?? || 
                  power?? || 
                  pcos?? || 
                  networkStatus?? || 
                  msglist?? || 
                  jobs??>
                  ,
             </#if>
        </#if>
        <#if allEnergy??>"all_energy": "${allEnergy}kW/h"
             <#if power?? || 
                  pcos?? || 
                  networkStatus?? || 
                  msglist?? || 
                  jobs??>
                  ,
             </#if>
        </#if>
        <#if power??>"power": "${power}W"
             <#if pcos?? || 
                  networkStatus?? || 
                  msglist?? || 
                  jobs??>
                  ,
             </#if>
        </#if>
        <#if pcos??>"pcos": "${pcos}"
             <#if networkStatus?? || 
                  msglist?? || 
                  jobs??>
                  ,
             </#if>
        </#if>
        <#if networkStatus??>"network_status": "<#if 1==networkStatus?number>on<#else>out</#if>"
             <#if msglist?? || 
                  jobs??>
                  ,
             </#if>
        </#if>
        <#if msglist??>
        "msgs": [
             <#list msglist as msg>
            "${msg!}"<#if msg_index lt msglist?size-1>,</#if>
             </#list>
        ]
             <#if jobs??>
                  ,
             </#if>
        </#if>
        <#if jobs??>
        "jobs": {
            <#if jobs.settingTemperature??>
            "setting_temperature": [
                <#list jobs.settingTemperature as d>
                {
                    <#if d.settingTemperature??>"setting_temperature": "${d.settingTemperature}"</#if>
                    <#if d.time?? || 
                         d.isholiday?? || 
                         d.monitoring??>
                         ,
                    </#if>
                    <#if d.time??>"time": "${d.time}"</#if>
                    <#if d.isholiday?? || 
                         d.monitoring??>
                         ,
                    </#if>
                    <#if d.isholiday??>"isholiday": ${d.isholiday?number}</#if>
                    <#if d.monitoring??>
                         ,
                    </#if>
                    <#if d.monitoring??>"monitoring": "${d.monitoring}"</#if>
                }<#if d_index lt jobs.settingTemperature?size-1>,</#if>
                </#list>
            ]
                 <#if jobs.powerStatus??>
                      ,
                 </#if>
            </#if>
            <#if jobs.powerStatus??>
            "power_status": [
                <#list jobs.powerStatus as d>
                {
                    <#if d.powerStatus??>"power_status": "<#if 1==d.powerStatus?number>on<#else>off</#if>"</#if>
                    <#if d.time?? || 
                         d.isholiday?? || 
                         d.monitoring??>
                         ,
                    </#if>
                    <#if d.time??>"time": "${d.time}"</#if>
                    <#if d.time?? || 
                         d.isholiday?? || 
                         d.monitoring??>
                         ,
                    </#if>
                    <#if d.isholiday??>"isholiday": ${d.isholiday?number}</#if>
                    <#if d.time?? || 
                         d.isholiday?? || 
                         d.monitoring??>
                         ,
                    </#if>
                    <#if d.monitoring??>"monitoring": "${d.monitoring}"</#if>
                }<#if d_index lt jobs.powerStatus?size-1>,</#if>
                </#list>
            ]
            </#if>
        }
        </#if>
    }
}
</#if>