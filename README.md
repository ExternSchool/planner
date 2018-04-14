# Visit Planner
## THIS IS A DRAFT VERSION

## Story
<a href="https://uk.wikipedia.org/wiki/Школа_екстернів_(Київ)">Extern School</a> is a general education state school, 
located in Kiev, Ukraine.</br>
The school offers <a href="https://en.wikipedia.org/wiki/External_degree">external</a> secondary education and meets the 
highest quality standards complying with licensing requirements and conditions for all three levels of accreditation 
according to the Ukrainian law.</br>
Hundreds of talented Ukrainian children receive here an ability to get secondary education while being young professional 
artists, musicians, sportsmen. Physically disabled, ill, as well as children who spend a lot of time with their parents 
abroad, study here too. A lot of kids complete their secondary school in shortened terms, becoming the youngest students 
of their universities.</br>
The school has highly qualified and progressive-minded staff, last years has been equipped with modern computers, 
but being state funded is not able to cover all expenses required to develop specialized software to modernize learning 
process.</br>
The abilities of the school are greatly limited by the lack of contemporary digital management tools. For example, 
at this moment before the children come to meet their teachers at the school to discuss something or to get advice, they 
or their parents have to:</br>
1. Personally visit the school, take one of numerous paper notebooks, placed in the box on the secretary's table 
(one for each teacher), and write down the name, subject, class and telephone number of the student in a timetable, 
manually drawn by their teacher before.</br>
2. As an option to a personal visit it is possible to call the school and... ask the secretary to do that job for you.

There is no surprise if some issues arise with such an appointment making process:
 * it is absolutely manually handled and offline, so it requires students/their parents/school secretary to be physically 
present at the school to set the time of the visit;</br>
 * teachers' schedules are not long-time fixed and time to time need to be manually arranged, so often you can find that 
in the teacher's notebook there is no timetable for the dates when the students are able to visit the school or the 
timetable is filled out for the time they are ready to; </br>
 * waiting while a teacher finishes manually planning their meetings for the next period of time, sometimes personally 
coordinating it with dozens of their students and their parents, makes planning horizon very short and unpredictable, 
hardly motivating self-studying children;</br>
 * occasionally parents/their student children have to waste a lot of their time making extra visits just because they 
were unable to coordinate their visits with more than one teacher of being present at the school, but busy with other 
appointments scheduled for that time;</br>
 * the same story happens when the students write their tests: the places have to be reserved beforehand to let the 
teachers be able to prepare everything needed for the test; sometimes teachers mistakenly print out wrong test papers 
 (you should try to understand those manually filled forms!) and young students receive extra troubles waiting for 
appropriate papers or trying to pass their tests with wrong ones;</br>
 * there is no other way to get to know about test results except personally meeting a teacher thus multiplying 
unnecessary visits to the school;</br>
 * and so on.
</br>

## Purpose and scope
This <a href="https://en.wikipedia.org/wiki/Pro_bono">pro bono</a> project is going to:
 * give the teachers modern tools to do their job in most effective way;
 * facilitate the process of scheduling school visits for the young students, their parents, teachers and school 
 administration;
 * let the teachers elaborate their timetables, organizing, quickly and easy modifying them if such need arises, 
 automatically providing support and notifying every dependent person;
 * let the students and their parents optimize their learning process, widening time planning horizon, reducing their 
 efforts to settle and take meetings with their teachers and school administration;
 * give the teachers tools to organize students' tests automatically forming suitable papers to print them out;
 * optionally provide effective tools to coordinate parents community, later adding appropriate features on the basis 
 of the databases formed;
</br>

## Stakeholders
### Hosts:
#### Administration and assistant persons: 
 * the principal - receives actual, former and future students and their parents, as well as other visitors, receives 
 primary documents and enrolls students to the school
 * vice-principals - meet actual students and their parents, coordinate individual study plans, receive filled 
 application forms, medical certificates, other documents, supply student's record-books, individual study plans etc;
 * the psychologist - meets actual and future students, their parents
 * administrator-in-charge - prepares and prints out students test forms according to the list of the students taking
 their tests, meets the students, organizes and conducts test session
 * the head of the school parents' committee, heads of class parents' committees meet students' parents
 
#### Teachers:
 * prepare study materials and test forms
 * meet students to consult them on the study subjects according to their appointments
 * as a result of students' interviews give them permissions to pass their tests
 * check out the results of the tests passed
 * evaluate and grade students performance
 
 Every host has 2 roles: they have their positions (principal/vice-principal/psychologist/teacher) AND they teach school
 subjects
 
### Visitors:
#### Students
 * study at home their school courses self-paced in correspondence with their study plans;
 * visit their school teachers for consultations according to their settled individual plans;
 * pass their tests and interviews to complete their studying;
 
#### Parents of the Students, other attendants
 * apply to the school for the student's enrollment, deal with their documents;
 * coordinate with administration study plans for their students;
 * organize and manage their children studying process;
 
## Common Requirements
### Usability requirements
 * UI should be user friendly, easy to understand and operate
 * should be safe: previous registration for anyone who receives personal data (names, phone numbers etc.) should be 
 confirmed with a registry code given by school staff, unregistered guests should receive common public data only
 * should be fun, motivating young students 
 * continuous usability testing is a preference

### Technical requirements 
#### Projects Composition
This project:
 * Visit Planner
 
Future projects:
 * Electronic Registrar: learning progress, achievements, store tests results, grades...
 * Teacher's Assistant: test preparation lists, learning materials and plans organizer
 * Electronic Diary. Student's time planner based on the records from Visit Planner DB
</br>

#### User Groups and Roles
 * Application and database administrators
##### Hosts
 * School personnel (administration, teachers, other staff) and parents' community representatives, organizing 
 learning process
 * Teachers, teaching their school subjects  
##### Visitors:
 * Students - registered and verified students
 * Guests - everyone who visits service without any authorization or registration, unverified users

### Environmental requirements
 * russian/ukrainian localization
 * Ukrainian holidays

### Support requirements
 * should not require operational maintenance
 * no support/devops team available - preferably serverless and/or cloud based service
 * minimal cost, preferably use free tier service providers
 
### Interaction requirements 
 * interacts via external RESTful API 


## Visit Planner Requirements
### Functional Requirements. User stories
This project has to provide the following features:</br>
 * schedule reception time for the principal, vice-principals, psychologist;
 * set every teacher's usual routines for a long period of time (semester or two), specifying the time when they are 
 present at school and ready to meet their students; 
 * hosts may have possibilities to adjust predefined schedule if such a necessity arises (illness, holidays so on); 
 * if hosts make changes to their timetables, visitors should be automatically notified by email/sms etc.;
 * hosts should be able to survey their visitors list, time slots available and occupied to plan their workload better;
 * every record should supply the following information about the visitor: date, time, student's given and family names, 
 year of study/class, discipline/course, email, telephone number;
 * anyone, who uses this service without registration, should be able to access timetables of the hosts (without
 visitor and their appointment details provided);
 * hosts and visitors have to be registered;
 * visitors may have an ability to examine schedules of their hosts, selecting suitable and available time slots 
 for their visit appointments;
 * visitors may have a possibility to cancel their appointments, thus the cleared time slots have to be available for 
 other visitors reservations;
 * if all the slots are occupied it should be provided an ability to enqueue a visitor for a notification sent when any 
 appointment been cancelled;
 * *optionally* 
 some sort of gamification could be used to motivate students and their parents to follow an established schedule, 
 make and cancel their appointments beforehand; for example, they could receive some points every time when they come 
 to school exactly when it was appointed; those score points could affect time slot availability notification queue, 
 informing first the students who have most score points; or some reservation quotas could be established for the most 
 punctual students, competitions could be organized and so on; 
</br>

### Domain model and database implementation (skeleton):
SQL persistent Entities:
 * host: *hostId*, first name, middle name, family name, mail, phone number, position, taught subjects list, room number, 
 time slots by days of week
 * visitor: *visitorId*, first name, middle name, family name, mail, phone number, class, subject/teacher pairs
 * timeslot: *timeslotId*, hostId, date, start time (inclusive), end time (exclusive) OR a time interval?
 * appointment: *appointmentId*, hostId, timeslotId, visitorId, flag: fixed appointment OR reserve queue record
 * year of study/grade/class: *classId*, subjects list
 * school subject: *subjectId*, name, year of study, semesters taught, per semester: { consultation hours, is there a 
 test to be passed } 
 * ...
 * guest: *guestId* - registered users with stored their: name, mail, phone number, 
 *optionally* *IP* to control for security reasons multiple appointment requests from the same IP 

alternative noSQL schema domain organization:
 * subject: subject_id, year of study/grade/class, per semester: (consultation hours, is there a test)
 * student: student_id, name, mail, phone number, class, list of:(**subject_id**, **teacher_id**) 
 * teacher: teacher_id, name, position, taught subjects list (**subject_id**), mail, phone number, optionally
  list of students: (**student_id**)
 * teachers time line (one for each one): teacher_line_id, **teacher_id**, list of time slots:
 (date, start time, end time, **student_id**, subject) -- available for students reservations
 * personnel: personnel_id, name, position
 * personnel time line (one for each one): personnel_line_id, **personnel_id**, list of time slots:
 (date, start time, end time, **guest_id**) -- available for guests appointments
 * guest: guest_id, name, mail, phone number,
 *optionally* *IP* to control multiple appointment requests from the same IP for security reasons
 
### Visit Planner External API
 * POST     /hosts                                              
 * GET      /hosts
 * GET      /hosts/{hostId}
 * DELETE   /hosts/{hostId}                                    
 * GET      /hosts/{hostId}/timeslots
 * POST     /hosts/{hostId}/timeslots                           
 * DELETE   /hosts/{hostId}/timeslots/{timeslotId}              
 * GET      /hosts/{hostId}/timeslots/{timeslotId}/appointments
 * GET      /visitors/{visitorId}/appointments
 * POST     /visitors/{visitorId}/appointments
 * DELETE   /visitors/{visitorId}/appointments/{appointmentId}
 
 #### External API Rights 
 Method |                            URI                      |  Admin  |  Host   | Visitor | Guest*
 ------ | --------------------------------------------------- | ------- | ------- | ------- | -------
 POST   | /hosts                                              |    x    |         |         | 
 GET    | /hosts                                              |    x    |    x    |    x    |    x
 GET    | /hosts/{hostId}                                     |    x    |    x    |    x    |    x
 DELETE | /hosts/{hostId}                                     |    x    |         |         | 
 GET    | /hosts/{hostId}/timeslots                           |    x    |    x    |    x    |    x
 POST   | /hosts/{hostId}/timeslots                           |    x    |    x    |         | 
 DELETE | /hosts/{hostId}/timeslots/{timeslotId}              |    x    |    x    |         | 
 GET    | /hosts/{hostId}/timeslots/{timeslotId}/appointments |    x    |    x    |    x    |    x
 GET    | /visitors/{visitorId}/appointments                  |    x    |    x    |    x    |    
 POST   | /visitors/{visitorId}/appointments                  |         |         |    x    |     
 DELETE | /visitors/{visitorId}/appointments/{appointmentId}  |         |         |    x    |      
 **to be discussed*

### ... Alternative Visit Planner External API
**Personnel:**
 * POST     /personnel                                              
 * GET      /personnel
 * GET      /personnel/{personnelId}
 * DELETE   /personnel/{personnelId}                                    
 * GET      /personnel/{personnelId}/timeslots
 * POST     /personnel/{personnelId}/timeslots                           
 * DELETE   /personnel/{personnelId}/timeslots/{timeslotId}              
 * GET      /personnel/{personnelId}/timeslots/{timeslotId}/appointment
 
**Teachers:**
 * POST     /teachers                                              
 * GET      /teachers
 * GET      /teachers/{teacherId}
 * DELETE   /teachers/{teacherId}                                    
 * GET      /teachers/{teacherId}/timeslots
 * POST     /teachers/{teacherId}/timeslots                           
 * DELETE   /teachers/{teacherId}/timeslots/{timeslotId}              
 * GET      /teachers/{teacherId}/timeslots/{timeslotId}/appointments
 
**Students:**
 * GET      /students/{studentId}/appointments
 * POST     /students/{studentId}/appointments
 * DELETE   /students/{studentId}/appointments/{appointmentId}
 
**Guests:**
 * GET      /guests/{guestId}/appointments
 * POST     /guests/{guestId}/appointments
 * DELETE   /guests/{guestId}/appointments/{appointmentId}

### User flows
The app should demonstrate contract (API) based following user flows:

#### Edge Service Identity Management
 * Register as a new user
 * Confirm registration code
 * Sign in (as a user who has already confirmed a registration code) -- visitor, host
 * Sign in (as a user who has not yet confirmed a registration code) -- as a guest
 * Re-send request for a registration code OR get a code with the student's record-book from a vice-principal -- TBD
 * Forgot password
 * Change password
 * Sign-out
 
#### Visit Planner Features
  * View list of hosts
  * Add a new host (Admin-only feature)
  * Delete a host (Admin-only feature)
  * View list of timeslots at a host
  * and so on according to the API

##### cloud infrastructure
 * AWS EC2, S3 -- 1 year free tier, best reaction to unstable loads
 * AWS RDS -- 1 year free tier OR...
 * MongoDB Atlas A0 at AWS -- 3 shards 512M database instantly free
 
##### microservices diagram 
![Microservices diagram](https://user-images.githubusercontent.com/10642971/38766137-7368b23a-3fd6-11e8-9679-69b12c7939f8.png)

##### server/client platform
 * Java 8, Spring Boot 2, Netflix OSS, okta, Swagger/Open API
 * Thymeleaf3 templates, Bootstrap4, CSS3 ...OR any other great framework ...OR AWS Lambda JS based client?

##### security
 * Spring Security 5.0
 * OAuth with {okta} (free tier up to 7000 active users)
 
##### clients
 * standard HTML5 web browser
 * *optional* custom RESTful applications 

