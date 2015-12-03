# polibox
This is polibox, my project for the Internet Applications exam (master in computer engineering at Politecnico di Torino).
The exam is finished (good result), now i want to finish this project in my free time.

The project is a real time sharing system, multi-user, multi-device, operative system independent. 
The tool parts are: 
- a java desktop application, that synchronize bidirectionally a directory from your file system to the server and receives realtime notifications; 
- a j2ee server web application that manage both synchronization and web contents;
- a single page application for the browser. 

it uses: 
spring
angularjs
hibernate

It's built on this platform:
tomcat 7
mysql 5
java 7


3-12-2015
Today cannot be used for real applications, as are still missing some parts, some of the most important are:
- the download of a configured client from the server application. Now the configuration is manual and the download does not exists 
- documentation is missing

To run it you must configure with your credentials:

this lines in the /src/main/webapp/WEB-INF/configuration/dao/persistence.preperties

jdbc.user=root
jdbc.pass=root


When you download the application the default credential are:
username: prova@prova.com
password: 123123

Pay attention, if you delete the polibox_client device, your client will not login anymore (you have to reconfigure manually).

The email gateway must be configured. Thus, if you want to add new account you need to do the convalidation manually.

I will finish this app on my free time. Help if you want.
