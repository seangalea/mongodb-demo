# mongodb-demo
This demo shows how mongodb can be used to have a dynamic resource structure.
The main resource in this demo is Token. Various fields can be included or omitted while the resource structure remains valid.
The presence of certain optional fields enable the resource to make use of certain features. For instance,
* tag: makes the token filterable by tag
* ttl: sets a deadline for the existence of the token

Any new fields can be added to a resource at the time of POSTing it. Business logic can be added at any time to the application in order to utilize the new fields.

# Setup Instructions
* docker volume create --name=mongodata
* docker run --name mongodb -v mongodata:/data/db -d -p 27017:27017 mongo
* run java application
