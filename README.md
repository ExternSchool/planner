All the files or any part of this project are released under Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International Public License.
See file https://github.com/ExternSchool/planner/blob/develop/LICENSE for full license details.

# Visit Planner
General project presentation: [https://externschool.github.io](https://externschool.github.io) (in Russian)

## 1. Story
<a href="https://uk.wikipedia.org/wiki/Школа_екстернів_(Київ)">Extern School</a> is a general education state school, 
located in Kyiv, Ukraine.</br>
The school offers <a href="https://en.wikipedia.org/wiki/External_degree">external</a> secondary education and meets the 
highest quality standards complying with licensing requirements and conditions for all three levels of accreditation 
according to the Ukrainian law.</br>
Hundreds of talented Ukrainian children receive here an ability to get secondary education while being young professional 
artists, musicians, sportsmen. Physically disabled, persons with serious health problems, as well as children who spend a lot of time with their parents 
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

## 2. Purpose and scope
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

## 3. Stakeholders
### 3.1. Hosts:
#### 3.1.1. School officials and assistants: 
 * the principal - receives actual, former and future students and their parents, as well as other visitors, receives 
 primary documents and enrolls students to the school
 * vice-principals - meet actual students and their parents, coordinate individual study plans, receive filled 
 application forms, medical certificates, other documents, supply student's record-books, individual study plans etc;
 * the psychologist - meets actual and future students, their parents
 * administrator-in-charge - prepares and prints out students test forms according to the list of the students taking
 their tests, meets the students, organizes and conducts test session
 * the head of the school parents' committee, heads of class parents' committees meet students' parents
 
#### 3.1.2. Teachers:
 * prepare study materials and test forms
 * meet students to consult them on the study subjects according to their appointments
 * as a result of students' interviews give them permissions to pass their tests
 * check out the results of the tests passed
 * evaluate and grade students performance
 
Any school official or assistant (principal/vice-principal/psychologist/librarian etc) can also be a teacher and vice versa.
 
### 3.2. Visitors:
#### 3.2.1. Students
 * study at home their school courses self-paced in correspondence with their study plans;
 * visit their school teachers for consultations according to their settled individual plans;
 * pass their tests and interviews to complete their studying;
 
#### 3.2.2. Parents of the Students, other attendants
 * apply to the school for the student's enrollment, deal with their documents;
 * coordinate with administration study plans for their students;
 * organize and manage their children studying process;
 
## 4. Common Requirements
### 4.1. Usability requirements
 * UI should be user friendly, easy to understand and operate
 * should be safe: previous registration for anyone who receives personal data (names, phone numbers etc.) should be 
 enabled with a registry code given by school staff, unregistered guests should receive common public data only
 * should be fun, motivating young students 
 * continuous usability testing is a preference

### 4.2. Technical requirements 
#### 4.2.1. Project Composition
This project:
 * Visit Planner
 
Future projects:
 * Electronic Registrar: learning progress, achievements, store tests results, grades...
 * Teacher's Assistant: test preparation lists, learning materials and plans organizer
 * Electronic Diary. Student's time planner based on the records from Visit Planner DB

#### 4.2.2. User Groups and Roles
 * Application and database administrators
##### 4.2.2.1. Hosts
 * School officials, teachers, assistants, parents' community representatives, organizing learning process
 * Teachers, teaching their school subjects  
##### 4.2.2.2. Visitors:
 * Students - registered and verified students
 * Guests - everyone who visits service without any authorization or registration, unverified users

### 4.3. Environmental requirements
 * ukrainian localization
 * Ukrainian holidays

### 4.4. Support requirements
 * should require minimal operational maintenance - no devops team available 
 * minimal cost, preferably use free tier service providers
 
### 4.5. Interaction requirements  
 * *TBD*

## 5. Visit Planner Requirements
### 5.1. Functional Requirements. User stories
This project has to provide the following features:</br>
 * schedule reception time for the principal, vice-principals, psychologist;
 * set every teacher's usual routines for a long period of time (planOneSemesterOne or two), specifying the time when they are 
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

### 5.2. Domain model and database implementation (skeleton).
 visit [Docs](https://externschool.github.io)
 
### 5.3. Visit Planner External API. Option #1. Concise.
 **TBD*
 
#### 5.3.1. External API Rights  
 **TBD*


### 5.5. User flows
The app should demonstrate following user flows:

#### 5.5.1. Edge Service Identity Management
 * Register as a new user
 * Confirm registration code
 * Sign in (as a user who has already enabled a registration code) -- visitor, host
 * Sign in (as a user who has not yet enabled a registration code) -- as a guest
 * Re-send request for a registration code OR get a code with the student's record-book from a vice-principal -- TBD
 * Forgot password
 * Change password
 * Sign-out
 
#### 5.5.2. Visit Planner Features
  * View list of hosts
  * Add a new host (Admin-only feature)
  * Delete a host (Admin-only feature)
  * View list of timeslots at a host
  * and so on according to the [Docs](https://externschool.github.io)

### 5.6. Cloud infrastructure
 * *TBD*

### 5.7. Server/client platform
 * Java 8, Spring Boot 2, Spring 5, Maven, Spring Data, Spring Web, Spring Security
 * Liquibase, H2, PostgreSQL
 * Mockito, AssertJ, JUnit, DbUnit
 * Thymeleaf3, Bootstrap4, CSS3

### 5.8. Security
 * Spring Security 
 * CSRF enabled
 * Sensitive Credentials stored in server environment variables with a service creation script.
 
### 5.9. Clients
 * standard HTML5 web browser
 * RESTful clients -- *TBD* 
 
