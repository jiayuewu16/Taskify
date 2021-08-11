# Taskify - Chores Management for Parents and Kids

## Overview
<img src="https://github.com/jiayuewu16/Taskify/blob/main/app/src/main/res/drawable/ic_taskify_logo_transparent.png" width=100>

### Description
Allows parents to set chores for their children, and for the children to mark those chores as complete and receive points that can be redeemed for real life rewards.

**Features:** allows the user to log in as a designated "parent" or "child" account from different devices. "Parent" accounts are able to set one-time or recurring weekly chores. "Child" accounts are able to mark these chores as complete, receiving in-game points. Parent accounts can designate point thresholds that correspond to real life rewards for the child.

**Child earning a reward:**

<img src="https://user-images.githubusercontent.com/85655773/129068778-eddcfdfb-c775-4d3a-90f9-7a51d0f904fb.gif" alt="child earn reward" width="600"/>

**Parent creating a task:**

<img src="https://user-images.githubusercontent.com/85655773/129068817-0d623ee5-8b19-41f8-af8b-ad70740ba00b.gif" alt="create task" width="600"/>

**Switching theme colors:**

<img src="https://user-images.githubusercontent.com/85655773/129068838-5612f40c-b5fd-42fb-b105-6ccab7ab9828.gif" alt="theme switching" width="600"/>

**Signing up:**

<img src="https://user-images.githubusercontent.com/85655773/129068868-e7242174-21f1-441f-8b6f-0bb32539da62.gif" alt="sign up" width="600"/>

**Share to Facebook:**

<img src="https://user-images.githubusercontent.com/85655773/129068891-2acf3809-3036-4581-8ac7-482ac597dc82.gif" alt="share to facebook" width="600"/>

### App Evaluation
- **Category:** Productivity
- **Mobile:** Mobile is essential for being able to input chores at any time, and also mark chores complete at any time, anywhere. Push notifications reminding people of chores are real-time.
- **Story:** Helps children associate chores with reward, creating better habits and a more positive outlook on mandatory tasks. Also helps the child build a schedule. Allows parents to assign chores to their children in a fun way.
- **Market:** Parents who wish to make chores rewarding for their children, as well as help their children build good habits and good routine. As well, children who wish to make their chores more fun can suggest the app to their parents.
- **Habit:** Parents use it often, perhaps daily, to check if their children completed their required chores. Children use the app every time they complete a chore to mark it complete. The app also sends push notifications when it is time to do a chore, so the child is reminded to check it often. The graphics are also dynamic and engaging, Once a routine is built around the app, both parents and children will check the app consistently.
- **Scope:** V1 allows a single user account to set and complete their own chores. The account can create and assign one-time or recurring chores and assign it to themselves, as well as upload a photo of a reward for specified point thresholds. They can mark their own chores complete and gain points. V2 uses a database to allow parent and child accounts to be on different devices. The parent account can create and assign chores and assign them to a specific child, as well as set points rewards and upload an image of that reward. The app shows the parent the chores each child has, as well as every child's point total. It also shows them points thresholds. The children accounts can mark their specific chores complete and gain personal in-game points. The app shows the child the chores they have been assigned, as well as their own points and the upcoming points thresholds.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User can create a new account.
* User can log into and out of accounts. Tasks are tied to account.
* User can create a task item with a title, description, and points (assigned to themselves by default)
* User can see all their task items.
* User can mark that task complete and earn a specified number of points.
* User can set that task to give a push notification at a specific time.
* User can set rewards at specific point threshholds that, when reached, displays a message that informs them that they have earned that reward.
* User can see all their rewards.
* User can upload images of rewards.
* Accounts are split between "parent" and "child" accounts, where parent accounts can assign tasks/points thresholds, and children cannot assign tasks or change points thresholds.
* Parent can be associated with a specific child account.

**Optional Nice-to-have Stories**
* Parent can be associated with multiple children
   * Parents can assign tasks to specific children.
   * Parents can assign points thresholds to specific children.
* Parent accounts can be created. Parents then create a child account and give a key to their child to create a linked child account.
* Parents can view details about specific children.
* Tasks can have a description.
* User can view details of tasks.
* Parents can edit tasks.
* User can set recurring tasks.
* User can view details of rewards.
* Parents can edit rewards.
* Rewards splash screen for the child when a child reaches a points threshold.
* User can log in with Facebook.
* User can link their existing account with Facebook.
* User can post earned rewards to Facebook.

### 2. Screen Archetypes

* Login Screen
    * User can log into accounts.
* Registration Screen
    * User can create a new account.
    * Accounts are split between "parent" and "child" accounts, where parent accounts can assign tasks/points thresholds, and children cannot assign tasks or change points thresholds.
* Tasks (Stream)
    * User can see all their task items.
    * User can mark a task complete and earn a specified number of points.
* Rewards (Stream)
    * User can see all their rewards.
* Task creation
    * User can create a task item with a title, description, and points (assigned to themselves by default)
    * User can set that task to give a push notification at a specific time.
* Rewards creation
    * User can set rewards at specific point threshholds that, when reached, displays a message that informs them that they have earned that reward.
    * User can upload images of rewards.
* Profile
    * Parent can be associated with a specific child account.
    * User can log out of account.

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Tasks
* Rewards
* Profile

**Flow Navigation** (Screen to Screen)

* Login Screen
    -> Tasks
* Registration Screen
    -> Tasks
* Tasks
    -> Task Creation [Parents only]
    -> [Future: Details screen for each task]
* Rewards
    -> Rewards Creation [Parents only]
    -> [Future: Details screen for each reward]
* Task Creation
    -> Tasks
* Rewards Creation
    -> Rewards
* Profile
    -> None


## Wireframes
<img src="https://github.com/jiayuewu16/Taskify/blob/main/Wireframe.jpg" width=600>

## Models

Task
|      Property            |      Type                                               |      Description                                                                                                                                                                                                                                                                                |
|--------------------------|---------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|     objectId             |     String                                              |     Unique id for the task (default field)                                                                                                                                                                                                                                                      |
|     taskName             |     String                                              |     The user-set name for the task, such as “wash   dishes.”                                                                                                                                                                                                                                    |
|     alarm            |     Alarm                                            |     The time a push notification goes out and whether the task is repeating or not.     Note: there is no “due time” because that   would disincentivize doing the chore after the due time has passed. If a due   time is necessary, the parent could put it in the title field or manually delete the task.    |
|     pointsValue          |     int                                                 |     The number of points the task is worth once   completed.                                                                                                                                                                                                                                    |
|     users    |     Pointer to User     (array of User for optional)    |     The user(s) who have been assigned this   task.                                                                                                                                                                                                                                             |

Alarm
|      Property            |      Type                                               |      Description                                                                      |
|--------------------------|---------------------------------------------------------|---------------------------------------------------------------------------------------|
|     objectId             |     String                                              |     Unique id for the alarm (default field)                                           |
|     date                 |     DateTime                                            |     The date on which the alarm should be first played.                        |
|     recurringFlag        |     Boolean                                             |     Whether the alarm should be recurring weekly.                                     |
|     recurringWeekdays    |     List of Boolean                                     |     The days of the week the alarm should recur on.                 |

Reward
|      Property            |      Type                                               |      Description                                                                      |
|--------------------------|---------------------------------------------------------|---------------------------------------------------------------------------------------|
|     objectId             |     String                                              |     Unique id for the reward (default field)                                          |
|     rewardName           |     String                                              |     The user-set name for the reward, such as “ice   cream cone.”                     |
|     rewardImage          |     File                                                |     The user-set image of the reward.                                                 |
|     pointsThreshold      |     int                                                 |     The points threshold that must be reached to   award this reward.                 |
|     users                |     Pointer to User     (array of User for optional)    |     The user(s) who have been assigned this reward.                                   |

User
|      Property            |      Type                                                          |      Description                                                                                                            |
|--------------------------|--------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------|
|     objectId               |     String                                                         |     The unique user id, such as “MZHjOacmrx”   (default field)                                                              |
|     username             |     String                                                         |     The user-set unique username.                                                                                           |
|     password             |     String                                                         |     The user-set password.                                                                                           |
|     isParent             |     Boolean                                                        |     Notes whether the account is a parent   account or not.                                                                 |
|     parent    |     Pointer to User         |     Only for child account; pointer to its associated   parent.    |
|     profileImage         |     File                                                           |     The user-set profile image.                                                                                             |
|     pointsTotal          |     int                                                            |     The number of points the user has   accumulated.                                                                        |
