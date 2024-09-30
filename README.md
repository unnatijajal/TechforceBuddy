# TechforceBuddy

**DataBase configuration**
1. Create the Database names "tfb".
2. Set your databse username and password in application.properties file.

Run TechforceBuddyUi 
Run TechforceBuddyBl 

To process the data pdf hit : 
/preProcess API

**Note: For pre process the data of pdf you have add all policy pdfs into the "src/main/resources/pdf" folder**

To train the modal hit the :
/trainModal API

**Note : Only admin can hit this API User does not have right for this. And for that you have enter one mannual entry of admin detail into the database table named User**

**Step for Queary search**

 For Query Search user have to login.If user does not have account then first create account then login into the system.
 Then write the query into the text box and then click the search button. And it will give the relavent output.
