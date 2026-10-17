# Defect Report: Location Module

**Application:** https://mcdelivery.co.in/ · **Reported by:** Mahima Yadav · **Found:** 15-Oct-2026, during Sprint 2 automation runs (Edge 155, Windows 11, 1366×900)

| Defect ID | Module | Defect summary | Steps to reproduce | Expected | Actual | Severity | Priority | Linked test cases | Assigned to | Status |
|---|---|---|---|---|---|---|---|---|---|---|
| DEF_LOC_01 | Location | Location bar sometimes never loads: the header stays on "-" or "Now" with skeleton loaders | 1. Open https://mcdelivery.co.in/ 2. Wait 45s or more | Location bar shows the store or location prompt (e.g. "McDonald's India, Tumkur / Please enter your location…") | Header shows only "-" or "Now". The banner and menu stay on skeleton loaders. Clicking the location bar or "Restaurants Nearby" does nothing. Reloading sometimes helps. | High | P1 | TC_LOC_03, 04, 05, 08, 09, 10 | Dev team (backend / API) | Open |
| DEF_LOC_02 | Location | "Select Delivery Address" panel stays on skeleton loaders for a long time before showing "Log In / Sign Up to Continue" | 1. Open the site as a guest 2. Click the location bar | Login CTA shown within a few seconds | Skeleton for about 7s in good runs; sometimes the CTA never appears | Medium | P2 | TC_LOC_05 | Dev team | Open |
| DEF_LOC_03 | Location | Restaurants Nearby stays on skeleton loaders when the browser location is shared | 1. Allow location (e.g. Delhi 28.63, 77.22) 2. Open "Restaurants Nearby" | List of nearby restaurants, or a "not served" message | Skeleton cards keep loading; nothing is shown after 12s | Medium | P2 | TC_LOC_10 (exploratory) | Dev team | Open |
| DEF_LOC_04 | Location / Login gate | Mobile number field (`type=tel`) accepts letters and special characters | 1. Location bar → Log In / Sign Up to Continue 2. Type `abcde` | Only digits accepted | `abcde` is accepted into the field. "Verify Mobile" correctly stays disabled. | Low | P3 | TC_LOC_11_C, 11_D | Dev team (frontend) | Open |

**Note:** DEF_LOC_01 to DEF_LOC_03 are intermittent. They may also be caused by backend throttling of repeated automated sessions. The framework reduces their impact by waiting up to 45s, reloading the page up to 2 times and retrying a failed test once, but the issue is still reported because a real user sees the same blank header.
