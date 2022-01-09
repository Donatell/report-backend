# Medical reporting application (backend part)
[Deutsch](./README_DE.md)

See the description of the frontend part here: [Link](https://github.com/Donatell/Report-frontend)

## Description and area of application
This project has been developed for a Russian clinic in order to automate the process of creating reports for medical examinations of enterprises (B2B).

In Russia, all organizations are required to perform a medical examination annually, as well as upon hiring. Legislatively there is a list of dangerous or harmful factors, which establishes which numbered factors oblige to undergo which examinations. This program allows the clinic to make different types of medical reports required by the Federal Service for Surveillance in Healthcare to fulfill the contract with the customer, based on a list of people (name, date of birth, numbers of harmful factors).

## Influence and efficiency
The application of this program allowed to prepare reporting documents 6 times faster for standard contracts, and 20 times faster for special contracts. This allows the company to take more orders and use employees more efficiently.

## Workflow
1. Upload a list of patients as an ".xlsx" file 
2. Give it a name
3. Select a module (there are different modules depending on the customer)
4. Select column headers in order to correlate columns with its content
5. specify the gender, if it was not possible to set it by name
6. Download reports

## Technical description of the backend part
I have decided to use **Java Spring** to develop an efficient and secure application. **Spring Boot** made it simplier to set the project up and start developing. Another advantage of Spring Boot is its great compatibility with **Docker** - dockerizing and hosting the project on a Debian server was smooth. Apart from the convenience of development, an important reason to use Java in this project was **Apache POI** - the Java API for Microsoft Documents. This library made it possible to generate reports in .xlsx format with a suitable layout. **MySQL** was chosen as the DBMS because it is reliable and suitable for the planned data structures. To secure the communication and improve development workflow I used **Hibernate ORM** entities. For the reason that this app is available on the Internet, I have integrated **Okta** to secure digital interactions and limit access to the app. The application makes use of **SSL** to secure transferred data. The server uses **gzip** responce compression to achieve faster page loading time.
