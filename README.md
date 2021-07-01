# Taskify - Chores Management for Parents and Kids

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Allows parents to set chores for their children, and for the children to mark those chores as complete and receive points that can be redeemed for real life rewards.
Features: allows the user to log in as a designated "parent" or "child" account from different devices. "Parent" accounts are able to set one-time or recurring daily, weekly, monthly, yearly, or every x hours/days chores. "Child" accounts are able to mark these chores as complete, receiving in-game points. Parent accounts can designate point thresholds that correspond to real life rewards for the child.

### App Evaluation
- **Category:** Productivity
- **Mobile:** Mobile is essential for being able to input chores at any time, and also mark chores complete at any time, anywhere. Push notifications reminding people of chores are real-time.
- **Story:** Helps children associate chores with reward, creating better habits and a more positive outlook on mandatory tasks. Also helps the child build a schedule. Allows parents to assign chores to their children in a fun way.
- **Market:** Parents who wish to make chores rewarding for their children, as well as help their children build good habits and good routine. As well, children who wish to make their chores more fun can suggest the app to their parents.
- **Habit:** Parents use it often, perhaps daily, to check if their children completed their required chores. Children use the app every time they complete a chore to mark it complete. The app also sends push notifications when it is time to do a chore, so the child is reminded to check it often. Once a routine is built around the app, both parents and children will check the app consistently.
- **Scope:** V1 allows a single user account to set and complete their own chores. The account can create and assign one-time or recurring chores and assign it to themselves, as well as upload a photo of a reward for specified point thresholds. They can mark their own chores complete and gain points. V2 uses a database to allow parent and child accounts to be on different devices. The parent account can create and assign chores and assign them to a specific child, as well as set points rewards and upload an image of that reward. The app shows the parent the chores each child has, as well as every child's point total. It also shows them points thresholds. The children accounts can mark their specific chores complete and gain personal in-game points. The app shows the child the chores they have been assigned, as well as their own points and the upcoming points thresholds.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User can create a chore item with a title, description, and points (assigned to themselves by default)
* User can see all their chore items.
* User can mark that chore complete and earn a specified number of points.
* User can set that chore to give a push notification at a specific time.
* User can set recurring chores.
* User can create a new account.
* User can log into and out of accounts. Chores are tied to account.
* User can set rewards at specific point threshholds that, when reached, displays a simple message (such as a Toast) that informs them that they have earned that reward.
* User can see all their rewards.
* Accounts are split between "parent" and "child" accounts, where parent accounts can assign chores/points thresholds, and children cannot assign chores or change points thresholds.
* Parents can assign chores to specific children.
* Parents can assign points thresholds to specific children.
* Parent and children accounts on different devices can communicate via databases.

**Optional Nice-to-have Stories**

* Parent accounts must be created. Parents then create a child account and give a key to their child to create a linked child account.
* Parents can view details about specific children.
* Parents can view details of chores.
* Parents can edit chores.
* Parents can view details of rewards.
* Parents can edit rewards.
* User can upload pictures of rewards and chores.
* Rewards splash screen for the child when a child reaches a points threshold.

### 2. Screen Archetypes

* Login Screen
    * User can log into accounts.
* Registration Screen
    * User can create a new account.
    * Accounts are split between "parent" and "child" accounts, where parent accounts can assign chores/points thresholds, and children cannot assign chores or change points thresholds.
    * Parent and children accounts on different devices can communicate via databases.
* Tasks (Stream)
    * User can see all their chore items.
    * User can mark a chore complete and earn a specified number of points.
* Rewards (Stream)
    * User can see all their rewards.
* Chore creation
    * User can create a chore item with a title, description, and points (assigned to themselves by default)
    * User can set that chore to give a push notification at a specific time.
    * User can set recurring chores.
    * Parents can assign chores to specific children.
* Rewards creation
    * User can set rewards at specific point threshholds that, when reached, displays a simple message (such as a Toast) that informs them that they have earned that reward.
    * Parents can assign points thresholds to specific children.
* Profile
    * User can log out of account.

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Tasks
* Rewards
* Account

**Flow Navigation** (Screen to Screen)

* Login Screen
    -> Tasks
* Registration Screen
    -> Tasks
* Tasks
    -> Chore Creation [Parents only]
    -> [Future: Details screen for each chore]
* Rewards
    -> Rewards Creation [Parents only]
    -> [Future: Details screen for each reward]
* Chore Creation
    -> Tasks
* Rewards Creation
    -> Rewards
* Profile
    -> None


## Wireframes
[Add picture of your hand sketched wireframes in this section]
<img src="YOUR_WIREFRAME_IMAGE_URL" width=600>

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
[This section will be completed in Unit 9]
### Models
[Add table of models]
### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
