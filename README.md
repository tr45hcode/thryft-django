Method 1: Oracle SQL Developer (Visual & Easiest)

This is the best method if you want to see the script before you run it and ensure there are no errors.

    Open SQL Developer and connect to your database.

    Open the File: Go to File > Open and select your .sql file.

    Check the Schema: Ensure the worksheet is connected to the correct user (top-right corner of the worksheet).

    Run the Script:

        Do NOT click the "Run Statement" button (the single play icon).

        Click the Run Script button (the icon with the small paper and a play button, or press F5).

    Watch the Script Output: The pane at the bottom will show you a log of every table created and every row inserted.

    [!TIP] If you see ORA-00955: name is already used by an existing object, it means the table already exists. You might need to add DROP TABLE table_name; lines at the top of your script if you want a fresh start.

Method 2: SQL*Plus Command Line (Fastest)

This is the professional way to do it, especially if the file is very large (SQL Developer can lag with huge files).

    Place the file: Put your .sql file in a simple folder (e.g., C:\db_work\export.sql).

    Open Command Prompt / Terminal.

    Log in to SQLPlus:
    Bash

    sqlplus your_username/your_password@localhost:1521/xe

    Run the file using the @ command:
    SQL

    @C:\db_work\export.sql

    Commit the changes: Oracle doesn't always "save" automatically after a script. Once the script finishes, type:
    SQL

    COMMIT;
    EXIT;

Common Issues & Troubleshooting
Error	Meaning	Solution
ORA-00942	Table or view does not exist	You are trying to insert data or drop a table that isn't created yet. Check the order of your script.
ORA-02291	Integrity constraint violated	You are trying to insert a "Child" record (like an Order) before the "Parent" record (like a User) exists.
SP2-0042	Unknown command	You likely have a typo in your SQL file or are trying to run a non-SQL command.
Important: Handling "Foreign Key" Order

If your script fails halfway through with Integrity Constraint Errors, it’s because of the order of the tables. You must import in this order:

    Level 1 (Parents): USER_TABLE

    Level 2 (Children): STAFF, CUSTOMER, PRODUCT

    Level 3 (Dependencies): ORDER_TABLE

    Level 4 (Grandchildren): ORDER_ITEM, PAYMENT

Verifying the Import

Once finished, run this quick check in your SQL worksheet to make sure your data actually arrived:
SQL

SELECT table_name, num_rows 
FROM user_tables 
ORDER BY num_rows DESC;
