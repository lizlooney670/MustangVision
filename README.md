# MustangVision
Vision Processing on a Kangaroo PC connected to the robot over JSON

Parts:

1. Processor

2. Calibration Utility

Processes:

1. Processor 
  a. Uses Java server sockets to create an MJPG server at the specified network ID number
  b. Uses a camera at the specified port number
  c. Uses the classes: JSON.java and Evelope.java to send data to the RoboRio
  d. Gets data to set port numbers and HSV values from two text files: "ports.txt" & "scalars.txt"
  
2. Calibration Utility
  a. Asks what port the camera is connected to
  b. Will allow you to test the bounding box utility on an image from the camera
  c. Set custom HSV values using arrow keys to calibrate
  
NOTE: YOU NEED TO INSTALL THE OPENCV DRIVERS (INSTRUCTIONS IN THE OPENCV_INSTALLATION FOLDER)

UPDATE: 11/10/2016
  Everything is complete except for the ability to send JSON data to the robot
  - Check JSON.java & Envelope.java for more information
