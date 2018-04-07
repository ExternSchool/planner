# visit planner

<a href="https://uk.wikipedia.org/wiki/Школа_екстернів_(Київ)">Kiev Extern School</a> is a general education state school, which offers <a href="https://en.wikipedia.org/wiki/External_degree">external</a> secondary education (all three levels of accreditation), located in Kiev, Ukraine.</br>
The school has highly qualified and progressive-minded staff but being state funded is not able to cover all expences required to modernize learning process, moving it online and automating routine procedures.</br>
This pro bono project is going to facilitate the process of scheduling school visits for the young students, their parents and teachers.
</br>
At this moment before the children come to meet their teacher at the school to discuss something or to get advice they or thier parents have to:</br>
1. Visit the school, take one of numerous paper notebooks, placed in the box on the secretary's table (one for each teacher), and write down the name of the student in a timetable, previously manually drawn by their teacher.</br>
2. As an option to a personal visit you can call the school and ask the secretary to do that job for you.</br>

There are a some issues arising with such an appointment making process:
 * it's not automated and offline, so it requres students/their parents/school secretary to be phisically present at the school to set the time of the visit;
 * teachers' schedules are not fixed and time to time they need to be manually arranged, so often you can find in the teacher's notebook no tables for the dates you are interested in, or there is no vacant place in the tables present; this way you can do nothing at all, just waiting while the teacher finishes planning their meetings for the next period of time; the planning horizon is very short and sometimes unpredictable;
 * occasionally parents or their student children have to waste their time making extra visits just because they were unable to coordinate their visits with the teachers, who are present at the school at the same time, but have other appointments scheduled;
 * the same story happens when the students write their tests: their places have to be reserved beforehand, so the teachers could prepare test materials; sometimes teachers mistakenly print out wrong papers and young students receive extra troubles waiting for appropriate papers or trying to pass their tests with wrong ones;
 * there is no other way to get to know about test results except personally meet a teacher thus multiplying unnecessary visits to the school;
 
## Stakeholders
1. Administration and support personnel: the principal, vice-principals, psychologist.
2. Teachers.
3. Students.
4. Students parents.

## Requirements
### Functional Requirements. User stories
This project has to provide the following features:</br>
 1. schedule reception time for the principal, vice-principals, psychologist;
 2. set for a lond period of time (semester or two) each teacher's usual schedule, specifying the time when they are present at school and ready to meet their students; later could be added appropriate rooms occupation arrangement;</br>
 * hosts may have a possibility to adjust predefined schedule if such a necessity arises (illness, vacations so on); optionally providing automated notification of the scheduled visitors (with email, sms etc.);</br>

### Usability requirements

### Technical requirements (e.g. security, network, platform, integration, client)
RESTful back-end microservices:
 * Visits scheduler
 * Tests and learning plans: learning achievements and tests results -- E.Registrar
 * Teacher's assistant: test preparation lists, plans
 * Student's time planner based on records from Visits scheduler -- E.Diary
UI

### Environmental requirements
### Support requirements
### Interaction requirements (e.g. how the product should work with other systems)

