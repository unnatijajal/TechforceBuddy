# Techforce Buddy

This is the project for processing and searching the data related to the different policy from the different policy's PDFs.

Following are the steps for how to set up and use the Techforce Buddy :

**DataBase configuration**

     1. Create the Database names "tfb".
     2. Set your databse username and password in application.properties file.

**How to run app?**

     1. Run the TechforceBuddyBlApplication as Java application.
     2. Runt the TechforceBuddyUiApplication as java appliction.

**Note: Only authorized admin can access the /preProcess and /trainModal API endpoint, you must manually add an admin entry to the User database table.** 

**To process the Pdfs(Admin only)** 

     1. To prepare PDFs for processing, place them in "src/main/resources/pdf"
     2. Hit the /preProcess API endpoint for pre processing

**Training the Modal(Admin only)**

     1. Hit the /trainModal API endpoint to train the modal.

**Query search**

     1. User must be logged in to search for the queries.
     2. If user not registred, user must create the an account before logging in.
     3. Once logged in, user can enter a query in the text box and click the search button to   
        retrieve relevant result.

 
