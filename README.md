# ShambaPay

#Phase 1

*Problem*
- Faster, efficient and reliable data capture by a supervisor of a tea farm
- Management of the Farm owners, supervisors and respective employees on the farm through a database
- Map existing csv files into a new database
- Basic account management for three user levels

Development scope for each user type

*ADMIN*
Is in charge of the entire system and has an overview of all data through the firebase portal
- Account login and password update
- Owner management (Adding, deleting and deactivating owners)
- Supervisor management (Adding, deleting and deactivating owners)
- Bulk csv Upload of employees
- Bulk csv upload of work

*OWNER*
Is created only by an admin and given access to manage only its employees and supervisors. 
All tasks i.e. report creation, payroll and analytics will be performed on its own employees and supervisors.
- Account login and password update
- Set job types with respective rates to each job

*SUPERVISOR*
Is only created by an Admin and cannot exist on its own. Its sole responsibility is to
- Account login and password update
- Capture data form employee(s) - who and how much tea picked on what day
- The data captured will ONLY cater for tea picked in weight. This will assume all employees are tea pickers

