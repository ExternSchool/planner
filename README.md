# visit planner

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
 primary documents and enrolls students to the school;
 * vice-principals - meet actual students and their parents, coordinate individual study plans, receive filled 
 application forms, medical certificates, other documents, supply students' gradebooks, individual study plans etc;
 * the psychologist - meets actual and future students, their parents;
 * administrator-in-charge - prepares and prints out students test forms according to the list of the students taking
 their tests, meets the students, organizes and conducts test session;
 * the head of the school parents' committee, heads of class parents' committees meet students' parents;
 
#### Teachers:
 * prepare study materials and test forms;
 * meet students to consult them on the study subjects according to their appointments;
 * as a result of students' interviews give them permissions to pass their tests;
 * check out the results of the tests passed;
 * evaluate and grade students performance;
 
### Visitors:
#### Students
 * study at home their school courses self-paced in correspondence with their study plans;
 * visit their school teachers for consultations according to their settled individual plans;
 * pass their tests and interviews to complete their studying;
 
#### Parents of the Students
 * apply to the school for the student's enrollment, deal with their documents;
 * coordinate with administration study plans for their students;
 * organize and manage their children studying process;
 
## Requirements
### Functional Requirements. User stories
This project has to provide the following features:</br>
 * schedule reception time for the principal, vice-principals, psychologist;
 * set for a long period of time (semester or two) every teacher's usual routines, specifying the time when they are 
 present at school and ready to meet their students; 
 * hosts may have possibilities to adjust predefined schedule if such a necessity arises (illness, vacations so on); 
 * if hosts make changes to their timetables, visitors should be automatically notified by email, sms etc.;
 * hosts should be able to survey their visitors list, time slots available and occupied to plan their workload better;
 * every record should supply the following information about the visit: date, time, name of the student, family name, 
 class, discipline/course, email, telephone number;
 * any person, who uses this service, should be able to access timetables of the principal and psychologist and make 
 their appointments without any preliminary registration;
 * hosts and visitors have to be registered, their registration has to be confirmed before they receive full rights;
 * visitors may have an ability to examine the schedule of their hosts, selecting suitable time for their visits;
 * visitors may have a possibility to cancel their appointment, thus the cleared time slot has to be available for other 
 reservations;
 * if all the slots are occupied it should be provided an ability to enqueue a visitor for a notification sent when any 
 appointment will be cancelled;
 * some sort of gamification could be used to motivate students and their parents to follow an established schedule, 
 make and cancel their appointments beforehand; for example, they could receive some points every time when they come 
 to school exactly when it was appointed; those score points could affect time slot availability notification queue, 
 informing first the students who have most score points; or some reservation quotas could be established for the most 
 punctual students, competitions could be organized and so on; 
</br>

### Usability requirements
 * easy to use
 * safe
 * fun
 * provide Continuous Usability Testing

### Technical requirements 

#### User Groups
 * Admins
 * Hosts - school administration, teachers, other staff, heads of parents committees 
 * Visitors - registered and verified students
 * Guests - registered but unverified users, everyone else, mostly parents

#### RESTful microservices
This project:
 * Visit Planner
 
Future projects:
 * Electronic Registrar: learning progress, achievements, store tests results, grades...
 * Teacher's Assistant: test preparation lists, learning materials and plans organizer
 * Electronic Diary. Student's time planner based on the records from Visit Planner DB
</br>

#### Database Entities:
 * host: *hostId, first name, middle name, family name, mail, phone number, position/subjects taught, room number, timeslots
 * visitor: *visitorId, first name, middle name, family name, mail, phone number, class, subject/teacher pairs
 * guest: *visitorId, first name, middle name, family name, mail, phone number
 * timeslot: *timeslotId, hostId, date, start time (inclusive), end time (exclusive) OR a time interval?
 * appointment: *appointmentId, hostId, timeslotId, visitorId
 * ...

##### security
 * Spring Security
 * SSO/tokens?

##### network
REST over http/https

##### platform
 * back-end - Java 8, Spring Boot 2
 * front-end - Java 8, Spring Boot 2, Thymeleaf templates?

##### Visit Planner API
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
 
 ##### API Rights 
 Method |                            URI                      |  Admin  |  Host   | Visitor | Guest
 ------ | --------------------------------------------------- | ------- | ------- | ------- | -------
 POST   | /hosts                                              |    x    |         |         | 
 GET    | /hosts                                              |    x    |    x    |    x    |    x
 GET    | /hosts/{hostId}                                     |    x    |    x    |    x    |    x
 DELETE | /hosts/{hostId}                                     |    x    |         |         | 
 GET    | /hosts/{hostId}/timeslots                           |    x    |    x    |    x    |    x
 POST   | /hosts/{hostId}/timeslots                           |    x    |    x    |         | 
 DELETE | /hosts/{hostId}/timeslots/{timeslotId}              |    x    |    x    |         | 
 GET    | /hosts/{hostId}/timeslots/{timeslotId}/appointments |    x    |    x    |    x    |    
 GET    | /visitors/{visitorId}/appointments                  |    x    |    x    |    x    |    
 POST   | /visitors/{visitorId}/appointments                  |    x    |    x    |    x    |    
 DELETE | /visitors/{visitorId}/appointments/{appointmentId}  |    x    |    x    |    x    |    

##### client
 * HTTP client
 * HTML5 based UI 
 * Thymeleaf templates?

### Environmental requirements
 * russian/ukrainian localization
 * Ukrainian holidays

### Support requirements
 * service should not require frequent maintainance
 
### Interaction requirements 
how the product should work with other systems

