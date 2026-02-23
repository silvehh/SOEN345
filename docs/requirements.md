# SOEN 345 - Ticket Reservation Application  
## Requirements Document

**Project:** Cloud-based Ticket Reservation Application  
**Document:** Functional & Non-Functional Requirements  
**Status:** Finalized  

---

## 1. Functional Requirements

### 1.1 Customer (End User) Requirements

| ID | Requirement | Description | Priority | Verification |
|----|-------------|-------------|----------|--------------|
| **FR1** | Register using email or phone | Users shall be able to register using either an email address or a phone number as the primary contact identifier. | Must | Unit test validation logic; UI test registration flow with email and phone. |
| **FR2** | View list of available events | Users shall be able to view a list of available events (e.g. movies, concerts, travel, sports). | Must | Unit test data layer; UI test event list display. |
| **FR3** | Search and filter events | Users shall be able to search and filter events by date, location, or category. | Must | Unit test filter logic; UI test search and filter controls. |
| **FR4** | Cancel reservations | Users shall be able to cancel their reservations. | Must | Unit test cancellation logic; integration test with persistence; UI test cancel flow. |
| **FR5** | Receive confirmations | Users shall receive confirmations via email or SMS after making or cancelling a reservation. | Must | Unit test notification service; integration test with email/SMS (or mock). |

### 1.2 Administrator / Event Organizer Requirements

| ID | Requirement | Description | Priority | Verification |
|----|-------------|-------------|----------|--------------|
| **Admin-FR1** | Add new event | Administrators shall be able to add a new event to the system. | Must | Unit test event creation; UI test add-event flow. |
| **Admin-FR2** | Edit existing event | Administrators shall be able to edit an existing event. | Must | Unit test update logic; UI test edit flow. |
| **Admin-FR3** | Cancel event | Administrators shall be able to cancel an event. | Must | Unit test cancel logic; UI test admin cancel flow. |

---

## 2. Non-Functional Requirements

| ID | Requirement | Description | Priority | Verification |
|----|-------------|-------------|----------|--------------|
| **NFR1** | Concurrency & performance | The system shall support concurrent users without performance degradation. | Must | Load / performance testing; stress tests. |
| **NFR2** | Cloud-based & availability | The system shall be cloud-based and ensure high availability. | Must | Architecture review; deployment and availability checks. |
| **NFR3** | Usability | The UI shall be simple and user-friendly. | Must | User testing; heuristic evaluation. |

---

## 3. Summary

- **Functional (Customer):** FR1-FR5  
- **Functional (Admin):** Admin-FR1-Admin-FR3  
- **Non-Functional:** NFR1-NFR3  

*Last updated: 22 February 2026. Requirements derived from SOEN 345 Winter 2026 project description.*
