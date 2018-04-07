# visit planner

<a href="https://uk.wikipedia.org/wiki/Школа_екстернів_(Київ)">Extern School</a> is a general education state school, 
located in Kiev, Ukraine. 
The school offers <a href="https://en.wikipedia.org/wiki/External_degree">external</a> secondary education and meets the 
highest quality standards complying with licensing requirements and conditions for all three levels of accreditation 
according to the Ukrainian law.</br>
Hundreds of talented Ukrainian children receive here an ability to get secondary education while being young professional 
artists, musicians, sportsmen. Physically disabled, ill, as well as children who spend a lot of time with their parents 
abroad, study here too. A lot of kids complete their secondary school in shortened terms, becoming the youngest students 
of their universities. </br>
The school has highly qualified and progressive-minded staff, last years has been equipped with modern computers, 
but being state funded is not able to cover all expenses required to modernize learning process, move it online and 
automate routine procedures. </br>
The abilities of the school are greatly limited by the lack of modern digital management tools. 
For example, at this moment before the children come to meet their teachers at the school to discuss something or to get 
advice, they or their parents have to:</br>
1. Personally visit the school, take one of numerous paper notebooks, placed in the box on the secretary's table 
(one for each teacher), and write down the name, subject, class and telephone number of the student in a timetable, 
manually drawn by their teacher before.</br>
2. As an option to a personal visit it is possible to call the school and... ask the secretary to do that job for you.

</br>
There is no surprise if some issues arise with such an appointment making process:
</br>

 * it is absolutely manually handled and offline, so it requires students/their parents/school secretary to be physically 
present at the school to set the time of the visit;</br>
 * teachers' schedules are not long-time fixed and time to time need to be manually arranged, so often you can find that 
in the teacher's notebook there is no timetable for the dates when the students are able to visit the school or the 
timetable is filled out for the time they are ready to; </br>
 * waiting while a teacher finishes manual planning their meetings for the next period of time, personally coordinating 
it with dozens of their students and their parents, makes planning horizon very short and sometimes unpredictable, 
hardly motivating self-studying children;</br>
 * occasionally parents/their student children have to waste a lot of their time making extra visits just because they 
were unable to coordinate their visits with more than one teacher of being present at the school, but busy with other 
appointments scheduled for the time when the student comes;</br>
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
 * let the hosts manage their timetables organizing and modifying them in the most effective way, providing support and 
 notification to every dependent person;
 * let the students and their parents better organize their learning, widening time planning horizon, reducing time and 
 efforts to settle meetings with their teachers and school administration;
 * 
 * provide effective tools to coordinate parents community;
 
</br>

## Stakeholders
#### Hosts:
1. Administration and support personnel: the principal, vice-principals, psychologist.
2. Teachers.
#### Visitors:
3. Students.
4. Students parents.

## Requirements
### Functional Requirements. User stories
This project has to provide the following features:</br>
 1. schedule reception time for the principal, vice-principals, psychologist;
 2. set for a lond period of time (semester or two) each teacher's usual schedule, specifying the time when they are present at school and ready to meet their students; later could be added appropriate rooms occupation arrangement;</br>
 * hosts may have a possibility to adjust predefined schedule if such a necessity arises (illness, vacations so on); 
 * optional: provide automated notification to the visitors (email, sms etc.);
 
</br>

### Usability requirements

### Technical requirements (e.g. security, network, platform, integration, client)
#### back-end
RESTful microservices:
 * Visits scheduler
 * Tests and learning plans: learning progress, achievements and tests results -- E.Registrar
 * Teacher's assistant: test preparation lists, learning materials and plans organizer
 * Student's time planner based on records from Visits scheduler -- E.Diary
</br>

#### front-end

client side UI

### Environmental requirements
### Support requirements
### Interaction requirements (e.g. how the product should work with other systems)

