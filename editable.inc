include "exploresensor.inc"

# You may edit the definition of robot_ranger.  
# You can change the position and direction of the exploresensors,
# add additional exploresensors or remove exploresensors.  Your 
# robot may possess up to 16 exploresensors (not more).  Do not
# remove the include line.

define robot_ranger ranger 
(
   exploresensor ( pose [ 0.25  0.25 0.1   0] )
   exploresensor ( pose [ 0.25  0.000 0.1   0] )
   exploresensor ( pose [ 0.25 -0.25 0.1   0] )
   exploresensor ( pose [ 0.40  0.125 0.1  90] )
   exploresensor ( pose [ 0.075 0.125 0.1  90] )
   exploresensor ( pose [ 0.125  0.125 0.1  30] )
   exploresensor ( pose [ 0.125  0.125 0.1  15] )
   exploresensor ( pose [ 0.125 -0.125 0.1 330] )
   exploresensor ( pose [ 0.55 0.25 0.1 270] )
)
