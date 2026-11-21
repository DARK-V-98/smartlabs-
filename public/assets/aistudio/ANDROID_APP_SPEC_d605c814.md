# SmartLabs Android App: Design Spec for the Example App

This file lists **every part of SmartLabs** that the Android app needs, so you can design an
example app that covers all of it. When it's done, send the file(s) back (Figma link, APK,
Expo project, or screenshots). I'll match it to the real backend (`smartlabs.lk` APIs plus the
same Firebase accounts, credits, and data).

> What exists today: `smartlabs-app/` (Expo + expo-router) has auth, 4 tabs (Home, Practice,
> Progress, Account), credits, history, mock, the PTE practice trainers, and settings. The new
> design replaces its look and navigation and adds everything else below.

---

## 1. Who uses the app

| Role | What they need | In the app? |
|---|---|---|
| **Student** (main user) | Practice PTE/IELTS, AI scoring, mocks, courses, recordings, bookings, credits | ✅ Full app |
| **Lecturer** | Their one-to-one availability and sessions, Zoom button | ✅ Small "Lecturer mode" area, shown only to lecturers |
| **Partner** (referral) | Referral code, earnings, tier | ⚪ Optional: one "Partner" screen, or a link to the web |
| **Admin / staff** | Everything in `/admin/dashboard` | ❌ Stays on the web |

---

## 2. Navigation (the most important part)

### 2.1 Bottom navigation bar: 5 tabs

| Tab | Icon idea | What's inside |
|---|---|---|
| **Home** | house | Greeting, credits strip, "continue where you left off", today's schedule, quick actions, offers/banners |
| **Practice** | target / pencil | PTE and IELTS practice hub (segmented switch at the top: **PTE \| IELTS**) |
| **Tests** | stopwatch | Mock tests (PTE full mocks, IELTS mocks), level test, AI score test, results |
| **Learn** | play / book | Courses, recorded sessions, live recordings, webinars, workshops, one-to-one, resources, blog |
| **Me** | avatar | Profile, progress, credits and purchases, settings, support, certificates |

Rules:
- Max **5 tabs**. Labels always visible, active tab filled with the primary colour.
- The tab bar hides inside full-screen test runners (mock tests, timed practice) so students can't tap out by accident. Leaving asks "Leave test? Your progress will be lost".
- Android back button: inside a tab it goes back one screen; on a tab root it goes to Home; on Home it shows "Press back again to exit".

### 2.2 Top app bar (every screen)
- Left: back arrow (sub-screens) or SmartLabs logo (tab roots).
- Centre: screen title.
- Right: **credits pill** (tap → Credits screen) and **notification bell** with a badge.

### 2.3 Extra navigation patterns to design
- **Segmented control**: PTE | IELTS on Practice and Tests.
- **Skill chips**: Speaking · Writing · Reading · Listening (horizontal scroll).
- **Search**: one global search icon on Practice and Learn (search tasks, courses, resources).
- **Deep links**: `smartlabs.lk/...` links and notification taps open the right screen.
- **Lecturer mode switch** (only for lecturers): a row in **Me** → "Switch to Lecturer view".

---

## 3. Every screen to design

✅ = must have in the example · ⚪ = nice to have

### 3.1 Start and account (no tab bar)
- ✅ **Splash**: logo animation (one exists: `src/ui/splash.tsx`)
- ✅ **Welcome / onboarding**: 2–3 slides (AI scoring, mocks, live classes) → Sign up / Log in
- ✅ **Log in**: email + password, "Forgot password", Google sign-in button
- ✅ **Sign up**: name, email, password, phone (optional), exam goal (PTE / IELTS), target score
- ✅ **Verify email**
- ✅ **Forgot password**
- ⚪ **Study plan setup**: exam date, target score → generates a plan (`/study-plan`)

### 3.2 Home tab
- ✅ Greeting + exam countdown ("PTE in 23 days")
- ✅ **Credits strip**: chips for each credit pool (see §5), tap → Credits
- ✅ Continue practising (last task)
- ✅ Weekly goal ring (sessions this week vs goal)
- ✅ Today / upcoming: live classes, one-to-one bookings, webinars
- ✅ Quick actions grid: Practice PTE, Practice IELTS, Mock test, AI Tutor, Book one-to-one
- ⚪ Offers / seasonal banner carousel (the web has seasonal themes)
- ⚪ Latest blog post card

### 3.3 Practice tab: PTE (segment 1)
Hub: skill chips → task list cards (name, question count, credit cost, "AI scored" badge).

**Speaking**
- ✅ Read Aloud · ✅ Repeat Sentence · ✅ Describe Image · ✅ Retell Lecture
- ✅ Answer Short Question · ✅ Respond to a Situation · ✅ Summarize Group Discussion

**Writing**
- ✅ Summarize Written Text (SWT): AI scored
- ✅ Write Essay: AI scored, plus an **AI essay tutor** chat
- ✅ SST (Summarize Spoken Text): AI scored
- ✅ WFD (Write From Dictation): AI scored

**Reading**
- ✅ R&W Fill in the Blanks (dropdown) · ✅ Fill in the Blanks (drag & drop)
- ✅ MCQ single · ✅ MCQ multiple · ✅ Re-order Paragraphs

**Listening**
- ✅ Listening Fill in the Blanks · ✅ MCQ single · ✅ MCQ multiple
- ✅ Highlight Correct Summary · ✅ Select Missing Word · ✅ Highlight Incorrect Words

**Shared trainer screen parts (design once, reuse for every task):**
- Header: task name, question X/Y, timer, credit cost
- Prompt area: text / image / **audio player** (play, replay limit, waveform)
- Answer area: one of **mic recorder** (prep countdown → recording bar → stop) · text box with word counter · dropdown blanks · drag chips · checkbox/radio options · draggable paragraph list
- Bottom bar: Previous · **Submit** · Next
- **Score result screen**: overall score ring + breakdown bars (content, fluency, pronunciation, grammar…), AI feedback text, model answer, "Try again"
- Locked state: "Needs 1 credit" → Buy credits

### 3.4 Practice tab: IELTS (segment 2)
- ✅ Listening: Cambridge 11–15, 20, 21 (test cards, 4 tests each)
- ✅ Reading: Cambridge 11–21 + **Skimming trainer**
- ✅ Writing: Cambridge 21 Task 1 / Task 2 + **AI IELTS essay practice** (band score feedback)
- ✅ Speaking: practice + speaking recordings history
- ✅ IELTS test runner: split view (passage / questions) on wide screens, swipe between them on phones

### 3.5 Tests tab
- ✅ **PTE mock tests**: Test 1, Test 2, Full Mock 1, Full Mock 2 (cards with duration, sections, credit/price, lock)
- ✅ **IELTS mock tests**: Mock 1–4 (L + R + W, overall band = average)
- ✅ **Full-screen exam runner**: section intro → timed questions → section break → submit
- ✅ **Results**: list of past results, then a detail screen (overall + per-skill scores, compare to target)
- ✅ **Level test**: 30-minute placement test → level result
- ✅ **AI score test**: writing tasks with instant AI score
- ⚪ PTE exam guide (read-only info screens)

### 3.6 Learn tab
- ✅ **Courses**: list → course detail (syllabus, lecturer, price, schedule) → **Enroll** (PayHere)
- ✅ **My courses / registrations**: enrolled batches, schedule
- ✅ **Recorded sessions**: packages → package detail → secure video player (no download, watermark with the student's email)
- ✅ **Class recordings**: list → player
- ✅ **One-to-one sessions**:
  - Lecturer list (photo, subjects, online/physical, price per hour)
  - Book: pick mode → hours (1/2/3) → date strip → **time slots grid (15-minute steps)** → pay
  - Note on screen: "A 15-minute break is kept between sessions"
  - **My bookings**: upcoming/past, status chip (held, confirmed, completed, cancelled), refund info, **Join Zoom** button (appears 30 min before)
- ✅ **AI Tutor**: course picker → chat screen (message bubbles, typing indicator)
- ✅ **Webinars**: list → register
- ✅ **Workshops**: list → detail → register
- ✅ **Resources**: essay topics 2026, workshop documents, downloadable PDFs
- ⚪ **Blog**: list → article
- ⚪ **Videos**: YouTube/lesson gallery
- ⚪ Grammar Clinic course, Feedback Club, Corporate training (info pages)

### 3.7 Me tab
- ✅ Profile header: photo, name, exam goal, target score, edit
- ✅ **Progress**: score trend chart per skill, practice streak, attempts history, weak areas
- ✅ **Credits & purchases**: balance per pool, buy packages (PayHere), purchase history / receipts
- ✅ **Schedule**: calendar of classes and bookings
- ✅ **Certificates**: request certificate, status, download
- ✅ **Support**: tickets list, new ticket, chat-style thread; plus WhatsApp / call / email contact
- ✅ **Settings**: notifications (reminders), sound, theme (light/dark/system), language, change password, **delete account** (Play Store requires it)
- ✅ Policies (terms, privacy, refund) · App guide · Log out
- ⚪ Partner area (referral code, share button, earnings, tier 10/12/15%)

### 3.8 Lecturer mode (only lecturers see it)
- ✅ Today's sessions with student name, mode, **Join Zoom** button
- ✅ Availability editor: add time windows, "repeat weekly ×N"
- ✅ Past sessions / payouts summary

### 3.9 System screens
- ✅ Payment success / payment cancelled
- ✅ Notifications list (reminders, booking confirmations, new content)
- ✅ No internet (with retry) · Maintenance mode (the web has a site-status page) · Force update
- ✅ Empty states (no results yet, no bookings), loading skeletons, error states

---

## 4. Reusable components to design

Design these once as a small kit; every screen above is built from them.

| Component | Variants |
|---|---|
| Buttons | Primary (gradient), Secondary (outline), Text, Icon, Disabled, Loading |
| Inputs | Text, password (show/hide), search, dropdown, OTP/code |
| Cards | Task card, course card, lecturer card, mock card (locked/unlocked), result card |
| Chips / tags | Skill tags (colour per skill), status chips, credit chips |
| Score | Score ring (0–90 PTE, 0–9 IELTS bands), breakdown bars, score header |
| Audio | Play button with progress, replay counter, **mic recorder** with live level |
| Timer | Countdown bar + mm:ss, red under 10 s |
| Sheets & dialogs | Bottom sheet (buy credits, filters), confirm dialog, leave-test dialog |
| Feedback | Toast/snackbar, skeleton loader, empty state art, error banner |
| Navigation | Bottom tab bar, top app bar, segmented control, horizontal chip scroller |

---

## 5. Data each screen shows (so the design has the right fields)

- **Credit pools** (each shown separately): Speaking · SST · SWT · Essay · IELTS essay · Mock test · IELTS mock · Universal
- **User**: name, email, photo, phone, exam (PTE/IELTS), target score, exam date, roles (student/lecturer/partner)
- **Attempt**: task type, date, score, breakdown, AI feedback
- **Booking**: lecturer, date, start–end (Sri Lanka time), mode, status, price (LKR), Zoom/address
- **Prices**: LKR (main site) · USD only for IELTS Studio

---

## 6. Design system

**Brand colours (from the website):**
| Token | Value | Use |
|---|---|---|
| Primary | `hsl(217 91% 60%)` ≈ `#3B82F6` blue | Buttons, active tab, links |
| Accent 1 | `hsl(347 89% 68%)` ≈ pink | Highlights, offers |
| Accent 2 | `hsl(159 70% 55%)` ≈ mint green | Success, good scores |
| Surface | white / `#F1F5F9` | Cards / screen background |
| Text | slate-900 / slate-500 | Main / secondary text |
| Danger | red-500 | Errors, timer warning |

- **Dark mode**: required. Give every colour a dark version.
- **Typography**: one family (e.g. Inter / Poppins). Sizes: 28 title · 20 heading · 16 body · 13 caption. Support Android font scaling up to 130% without breaking.
- **Spacing**: 4-pt grid (4, 8, 12, 16, 24, 32). Screen side padding 16.
- **Corners**: 12 for cards, 999 for chips and pills.
- **Icons**: one set only (Lucide is already used on the web).
- **Touch targets**: at least 48 × 48 dp. Contrast WCAG AA (4.5:1 for text).

---

## 7. Responsive rules

| Width | Layout |
|---|---|
| **< 600 dp** (phones) | Bottom tab bar, single column, full-width cards |
| **600–840 dp** (small tablets, foldables open) | **Navigation rail** on the left instead of the bottom bar, 2-column grids |
| **> 840 dp** (tablets landscape) | Navigation rail + **list–detail** (e.g. results list left, detail right); IELTS reading shows passage and questions side by side |

Also:
- Handle portrait **and** landscape. Exam runners can lock portrait on phones.
- Respect the status bar, gesture bar, and display cutouts (edge-to-edge, safe areas).
- Keyboard must not cover the input (essay / summary boxes scroll up).
- Small phones (360 × 640) must still fit the trainer's bottom bar without overlap.

---

## 8. What to send back with the example app

1. Screens for at least the ✅ items (Figma, APK, or an Expo project).
2. The component kit (§4) with light and dark colours.
3. The navigation you chose (if you change the 5 tabs, say why).
4. Phone + tablet versions of at least: Home, Practice hub, one trainer, Score result, Mock runner.
5. Any new feature ideas, marked as "new" so I can check the backend supports it.

I'll then connect it to the real APIs (`/api/questions`, `/api/score-*`, PayHere, `/api/one-to-one/*`,
Firestore `users/{uid}`) and keep accounts and credits shared with the website.
