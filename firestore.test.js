const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_uid_001";
const BOB_UID = "bob_uid_002";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function validProfilePayload(uid, now) {
  return {
    userId: uid,
    displayName: "Vanessa Perera",
    email: "vanessa@smartlabs.lk",
    examGoal: "PTE",
    targetScore: 79.0,
    examDateLabel: "In 23 days",
    weeklyGoalSessions: 12,
    sessionsThisWeek: 5,
    streakDays: 6,
    role: "student",
    lecturerViewEnabled: false,
    biometricEnabled: true,
    creditsSpeaking: 8,
    creditsSst: 5,
    creditsSwt: 5,
    creditsEssay: 4,
    creditsIeltsEssay: 3,
    creditsMock: 2,
    creditsIeltsMock: 2,
    creditsUniversal: 10,
    createdAt: now,
    updatedAt: now,
  };
}

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated users cannot read or write profiles or subcollections", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const past = new Date(Date.now() - 2000);
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).set(validProfilePayload(ALICE_UID, past)));
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("attempts").get());
});

test("Owner can create and partially update profile; rejects ghost fields, role escalation, and cross-user access", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const created = new Date(Date.now() - 5000);

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set(validProfilePayload(ALICE_UID, created))
  );

  // Partial update of goal & credits succeeds
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      examGoal: "IELTS",
      targetScore: 8.0,
      biometricEnabled: false,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject role escalation
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      role: "admin",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject ghost field
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      isVerified: true,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject future timestamp
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      targetScore: 7.5,
      updatedAt: new Date(Date.now() + 60000),
    })
  );

  // Reject cross-user read/write
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).update({ displayName: "Hack", updatedAt: new Date(Date.now() - 1000) }));
  await assertFails(aliceDb.collection("users").get());
});

test("Attempts, bookings (with terminal state lock), tutor messages, and support tickets enforce ownership and schema", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const past = new Date(Date.now() - 2000);

  // Create attempt
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("attempts").doc("att_1").set({
      userId: ALICE_UID,
      examType: "PTE",
      skill: "Writing",
      taskTitle: "Write Essay",
      overallScore: 81.0,
      maxScore: 90.0,
      contentScore: 82.0,
      fluencyScore: 80.0,
      pronunciationScore: 79.0,
      grammarScore: 84.0,
      userResponse: "Technology accelerates modern education...",
      aiFeedback: "Strong thesis and cohesive paragraph transitions.",
      modelAnswer: "Model 90 essay structure...",
      durationSeconds: 920,
      createdAt: past,
      updatedAt: past,
    })
  );

  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("attempts").get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("attempts").get());

  // Create booking and test terminal state locking
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("bookings").doc("bk_1").set({
      userId: ALICE_UID,
      bookingType: "ONE_TO_ONE",
      title: "1-on-1 PTE Speaking Clinic",
      lecturerName: "Dr. Kavinda Jayawardena",
      dateLabel: "Nov 22, 2026",
      timeSlot: "19:00 - 20:00 IST",
      mode: "Online",
      status: "confirmed",
      priceLkr: 4500,
      zoomUrl: "https://zoom.us/j/smartlabs-clinic",
      createdAt: past,
      updatedAt: past,
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("bookings").doc("bk_1").update({
      status: "cancelled",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Terminal state 'cancelled' cannot be updated again
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("bookings").doc("bk_1").update({
      status: "confirmed",
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Tutor message & Support ticket
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("tutor_messages").doc("msg_1").set({
      userId: ALICE_UID,
      courseContext: "PTE Academic Mastery",
      sender: "user",
      messageText: "How do I improve Repeat Sentence fluency?",
      createdAt: past,
      updatedAt: past,
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("support_tickets").doc("tk_1").set({
      userId: ALICE_UID,
      subject: "Certificate Request - PTE Intensive",
      category: "Certificate",
      status: "open",
      latestMessage: "Completed all modules, requesting verified digital certificate.",
      createdAt: past,
      updatedAt: past,
    })
  );
});
