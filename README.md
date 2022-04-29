# About project

This is a school project. My goal is to crate a TCP server that navigates remotely connected robots in a cartesian coordinates system to [0:0] and pick up a message there. The robots can be blocked on their way by obstacles, which we need to dodge and they also often parse their messages.

TODO:
- Rework messages to be sent as serialized message objects instead of text - CURRENTLY IN PROGRESS
- Change server messages about online clients to be sent each time the server online database changes instead of periodical update based on time - TBD
- Change the GUI to update online list from FX thread avoiding the exceptions - TBD
- Make login window in the GUI work - TBD
