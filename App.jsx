import React, { useEffect, useState } from "react";
import { Button, PermissionsAndroid, SafeAreaView, StatusBar, StyleSheet, Text, View, Alert } from "react-native";
import { NativeModules } from 'react-native';

const { CallRecorder } = NativeModules;

const requestPermission = async (permission, title, message) => {
  try {
    const granted = await PermissionsAndroid.request(
      permission,
      {
        title,
        message,
        buttonNeutral: "Ask Me Later",
        buttonNegative: "Cancel",
        buttonPositive: "OK"
      }
    );
    return granted === PermissionsAndroid.RESULTS.GRANTED;
  } catch (err) {
    console.warn(err);
    return false;
  }
};

const requestPermissions = async () => {
  const readPhoneState = await requestPermission(
    PermissionsAndroid.PERMISSIONS.READ_PHONE_STATE,
    "Phone State Permission",
    "This app needs access to your phone state to detect calls."
  );

  const recordAudio = await requestPermission(
    PermissionsAndroid.PERMISSIONS.RECORD_AUDIO,
    "Audio Recording Permission",
    "This app needs access to your microphone to record calls."
  );

  const permissionsStatus = {
    readPhoneState,
    recordAudio,
  };

  console.log("Permissions status:", permissionsStatus);
  return permissionsStatus;
};

const startRecording = () => {
  if (CallRecorder) {
    console.log("CallRecorder module:", CallRecorder);
    if (CallRecorder.startRecording) {
      CallRecorder.startRecording();
    } else {
      console.warn("startRecording method is not available on CallRecorder.");
    }
  } else {
    console.warn("CallRecorder module is not initialized or imported.");
  }
};


const stopRecording = () => {
  CallRecorder.stopRecording();
};

const App = () => {
  const [permissionsGranted, setPermissionsGranted] = useState(false);

  useEffect(() => {
    (async () => {
      const status = await requestPermissions();
      setPermissionsGranted(!Object.values(status).includes(false));
    })();
  }, []);

  return (
    <SafeAreaView style={styles.container}>
      <Text style={styles.item}>Call Recording App1</Text>
      <Button 
        title="Start Recording" 
        onPress={startRecording} 
        disabled={!permissionsGranted} 
      />
      <Button 
        title="Stop Recording" 
        onPress={stopRecording} 
        disabled={!permissionsGranted} 
      />
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    paddingTop: StatusBar.currentHeight,
    backgroundColor: "#ecf0f1",
    padding: 8,
  },
  item: {
    margin: 24,
    fontSize: 18,
    fontWeight: "bold",
    textAlign: "center",
  },
});

export default App;
