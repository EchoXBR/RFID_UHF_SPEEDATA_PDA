var uhf = {
    openDev: function(successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, 'rfidUbx', 'openDev', []);
    }
    ,
    inventoryStart: function(successCallback, errorCallback, tags) {
        cordova.exec(successCallback, errorCallback, 'rfidUbx', 'inventoryStart', tags);
    }
    ,
    closeDev: function(successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, 'rfidUbx', 'closeDev', []);
    }
    ,
    readArea: function(successCallback, errorCallback, tags) {
        cordova.exec(successCallback, errorCallback, 'rfidUbx', 'readArea', tags);
    }
    ,
    writeArea: function(successCallback, errorCallback, tags) {
        cordova.exec(successCallback, errorCallback, 'rfidUbx', 'writeArea', tags);
    }
    ,
    setAntennaPower: function(successCallback, errorCallback, tags) {
        cordova.exec(successCallback, errorCallback, 'rfidUbx', 'setAntennaPower', tags);
    }
    ,
    getAntennaPower: function(successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, 'rfidUbx', 'getAntennaPower', []);
    }
    ,
    setFreqRegion: function(successCallback, errorCallback, tags) {
        cordova.exec(successCallback, errorCallback, 'rfidUbx', 'setFreqRegion', tags);
    }
    ,
    getFreqRegion: function(successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, 'rfidUbx', 'getFreqRegion', []);
    }
    
};
module.exports = uhf;