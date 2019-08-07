# CSC251FinalProject
This is the final project for Forsyth Technical Community College's CSC-251 Java class. It is a GUI program for a coffee shop that connects to a local database to perform various tasks on that database. The user can add a new customer, add an order to an existing customer, view all customer's data, or view all orders by a specific customer via SQL querying.
## Things to Note
This program used a local database using the JDBC Derby Driver. Due to this, you will **HAVE to have NetBeans 8.X** (I used NetBeans 8.2) with the Derby Driver installed. Newer versions do not use this driver and, therefor, will not work as intended with this program. The local database **NEEDS to be hosted on port 1527** and **BE named dbCoffeeStoreData.** I have included a folder that has the database that I personally used, but there is a button on the main page (as per the requirements for this program) to create the tables in the database once there is a connected, local Derby database on the system with the same name as mentioned previously.
## Screenshots
Note: These were taken on a Linux Mint 19.1 system and a 4K monitor. Because of this, the control's sizes will not be quite as small on a 1080p monitor or other operating systems with better 4K support.
![Main Screen](/Screenshots/NewOrder.jpg?raw=true "Main Screen")
![Add Customer Form](/Screenshots/AddCustomer.jpg?raw=true "Add Customer Form")
![New Order Form](/Screenshots/NewOrder.jpg?raw=true "New Order Form")
![Display Customers Form](/Screenshots/DisplayCustomers.jpg?raw=true "Display Customers Form")
![Filter Orders by Customer Form](/Screenshots/OrdersByCustomer.jpg?raw=true "Filter Orders by Customer Form")
