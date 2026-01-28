📂 Thryft - E-Commerce Management System

A robust E-Commerce platform built with Django and Oracle Database. This system features role-based access for Customers and Staff, real-time stock management, and a comprehensive order processing workflow.
🚀 Getting Started
1. Prerequisites

Before you begin, ensure you have the following installed:

    Python 3.10+

    Oracle Database (XE or Enterprise Edition)

    Oracle Instant Client (Required for the oracledb / cx_Oracle driver)

    Git

2. Clone the Repository
Bash

git clone https://github.com/yourusername/thryft-project.git
cd thryft-project

3. Setup Virtual Environment

It is highly recommended to use a virtual environment to keep dependencies isolated.
Bash

# Create the environment
python -m venv venv

# Activate it (Windows)
venv\Scripts\activate

# Activate it (Mac/Linux)
source venv/bin/activate

4. Install Dependencies

Install all required Python packages using the requirements.txt file provided.
Bash

pip install -r requirements.txt

🗄️ Database Setup (Oracle)
Importing the .sql Data

If you have a database export file (e.g., backup.sql), follow these steps to import the schema and data into your Oracle instance.
Option A: Using SQL Developer (Recommended)

    Open Oracle SQL Developer and connect to your database.

    Go to File > Open and select your .sql file.

    Ensure the connection is active in the top-right corner.

    Press F5 (or the "Run Script" icon) to execute the entire file.

    Check the script output for any errors and ensure you see "Commit Complete."

Option B: Using Command Line (SQLPlus)
Bash

sqlplus your_username/your_password@localhost:1521/xe @path/to/your/backup.sql

⚙️ Configuration

    Database Connection: Open settings.py and ensure the DATABASES section matches your Oracle credentials:
    Python

    DATABASES = {
        'default': {
            'ENGINE': 'django.db.backends.oracle',
            'NAME': 'localhost:1521/xe',
            'USER': 'YOUR_USERNAME',
            'PASSWORD': 'YOUR_PASSWORD',
        }
    }

    Migrations: Even though the models are managed = False, Django needs to initialize the session and admin tables.
    Bash

    python manage.py migrate

🏃 Running the Application

Once the database is ready and dependencies are installed, start the development server:
Bash

python manage.py runserver

The application will be available at: http://127.0.0.1:8000/
🛠️ Project Structure

    /main: Core application logic (Views, Models, Templates).

    /static: CSS, JavaScript, and Images.

    requirements.txt: List of Python dependencies.

    README.md: Project documentation.
