package com.example.data

object SmartLabsCatalog {

    val practiceTasks: List<PracticeTaskItem> = listOf(
        // PTE Speaking (7 core tasks from spec)
        PracticeTaskItem(
            id = "pte_ra",
            examType = ExamType.PTE,
            skill = SkillCategory.SPEAKING,
            title = "Read Aloud",
            subtitle = "Speak clearly with natural phrasing and stress",
            questionCount = 42,
            creditCost = 1,
            creditPool = CreditPoolType.SPEAKING,
            aiScored = true,
            mode = TrainerInteractionMode.SPEAKING_MIC,
            timeLimitSeconds = 40,
            prepSeconds = 10,
            promptText = "Read the following academic passage aloud smoothly at a natural pace:",
            passageOrTranscript = "Urban microclimates are shaped by surface reflectivity, vegetation canopy, and airflow corridors. Strategic green roofs measurably lower peak summer street temperatures while capturing stormwater runoff before it overwhelms municipal drainage.",
            modelAnswerHint = "Emphasize content nouns ('microclimates', 'reflectivity', 'canopy'), pause briefly at commas, and maintain steady cadence."
        ),
        PracticeTaskItem(
            id = "pte_rs",
            examType = ExamType.PTE,
            skill = SkillCategory.SPEAKING,
            title = "Repeat Sentence",
            subtitle = "Listen once and repeat verbatim with fluency",
            questionCount = 65,
            creditCost = 1,
            creditPool = CreditPoolType.SPEAKING,
            aiScored = true,
            mode = TrainerInteractionMode.SPEAKING_MIC,
            timeLimitSeconds = 15,
            prepSeconds = 3,
            promptText = "Tap Play Audio, listen carefully, and repeat the exact sentence:",
            passageOrTranscript = "The university library will extend its evening hours throughout the final examination period.",
            modelAnswerHint = "Chunk into three rhythmic phrases: 'The university library / will extend its evening hours / throughout the final examination period.'"
        ),
        PracticeTaskItem(
            id = "pte_di",
            examType = ExamType.PTE,
            skill = SkillCategory.SPEAKING,
            title = "Describe Image",
            subtitle = "Summarize visual trends, extremes, and conclusion",
            questionCount = 34,
            creditCost = 1,
            creditPool = CreditPoolType.SPEAKING,
            aiScored = true,
            mode = TrainerInteractionMode.SPEAKING_MIC,
            timeLimitSeconds = 40,
            prepSeconds = 15,
            promptText = "Describe the Renewable Grid Adoption chart (Solar 48%, Wind 31%, Hydro 15%, Geothermal 6% between 2018 and 2026):",
            passageOrTranscript = "Chart Metrics — 2018 vs 2026: Solar grew from 18% to 48% (highest share). Wind rose steadily from 22% to 31%. Hydro remained stable around 15%, while Geothermal stood lowest at 6%.",
            modelAnswerHint = "State chart title, highest & lowest values, key trend from 2018 to 2026, and a crisp one-sentence conclusion."
        ),
        PracticeTaskItem(
            id = "pte_rl",
            examType = ExamType.PTE,
            skill = SkillCategory.SPEAKING,
            title = "Retell Lecture",
            subtitle = "Synthesize key lecture arguments in 40 seconds",
            questionCount = 28,
            creditCost = 1,
            creditPool = CreditPoolType.SPEAKING,
            aiScored = true,
            mode = TrainerInteractionMode.SPEAKING_MIC,
            timeLimitSeconds = 40,
            prepSeconds = 10,
            promptText = "Listen to the lecture excerpt on Behavioral Economics and retell the main insights:",
            passageOrTranscript = "Professor Sen highlights that default enrollment dramatically increases retirement savings because individuals face decision fatigue when presented with dozens of complex investment tiers.",
            modelAnswerHint = "Cover topic, default enrollment mechanism, decision fatigue cause, and measurable impact on savings."
        ),
        PracticeTaskItem(
            id = "pte_asq",
            examType = ExamType.PTE,
            skill = SkillCategory.SPEAKING,
            title = "Answer Short Question",
            subtitle = "Respond with 1–3 precise words immediately",
            questionCount = 50,
            creditCost = 1,
            creditPool = CreditPoolType.SPEAKING,
            aiScored = true,
            mode = TrainerInteractionMode.SPEAKING_MIC,
            timeLimitSeconds = 10,
            prepSeconds = 2,
            promptText = "Listen to the question and give a concise spoken answer:",
            passageOrTranscript = "What instrument is used by astronomers to observe distant galaxies and stars?",
            modelAnswerHint = "Telescope."
        ),
        PracticeTaskItem(
            id = "pte_rts",
            examType = ExamType.PTE,
            skill = SkillCategory.SPEAKING,
            title = "Respond to a Situation",
            subtitle = "Practical academic & workplace communication",
            questionCount = 24,
            creditCost = 1,
            creditPool = CreditPoolType.SPEAKING,
            aiScored = true,
            mode = TrainerInteractionMode.SPEAKING_MIC,
            timeLimitSeconds = 40,
            prepSeconds = 10,
            promptText = "Your research group's dataset is corrupted two days before submission. Address your supervisor politely and propose a recovery plan:",
            passageOrTranscript = "Situation: You discovered an uncorrupted backup from 48 hours ago and need a 24-hour extension to re-run statistical validation.",
            modelAnswerHint = "Acknowledge responsibility, explain the verified backup available, and request a specific 24-hour timeline politely."
        ),
        PracticeTaskItem(
            id = "pte_sgd",
            examType = ExamType.PTE,
            skill = SkillCategory.SPEAKING,
            title = "Summarize Group Discussion",
            subtitle = "Capture differing speaker viewpoints clearly",
            questionCount = 18,
            creditCost = 1,
            creditPool = CreditPoolType.SPEAKING,
            aiScored = true,
            mode = TrainerInteractionMode.SPEAKING_MIC,
            timeLimitSeconds = 40,
            prepSeconds = 10,
            promptText = "Listen to the three-speaker seminar on AI grading tools and summarize consensus & concerns:",
            passageOrTranscript = "Speaker A praises instant rubric feedback; Speaker B warns about cultural bias in idioms; Speaker C proposes human-in-the-loop moderation for borderline scripts.",
            modelAnswerHint = "Mention all three viewpoints and conclude with the hybrid human-moderation proposal."
        ),

        // PTE Writing (SWT, Essay, SST, WFD)
        PracticeTaskItem(
            id = "pte_swt",
            examType = ExamType.PTE,
            skill = SkillCategory.WRITING,
            title = "Summarize Written Text",
            subtitle = "Condense the passage into one single sentence (5–75 words)",
            questionCount = 36,
            creditCost = 1,
            creditPool = CreditPoolType.SWT,
            aiScored = true,
            mode = TrainerInteractionMode.WRITING_TEXT,
            timeLimitSeconds = 600,
            promptText = "Summarize the passage below in ONE single sentence between 5 and 75 words:",
            passageOrTranscript = "Deep-sea hydrothermal vents host thriving chemosynthetic ecosystems completely independent of sunlight. Microorganisms oxidize hydrogen sulfide into organic nutrients, sustaining giant tubeworms and blind shrimp under crushing pressure. Astrobiologists study these vents because similar chemical gradients may exist beneath the icy crusts of Europa and Enceladus.",
            modelAnswerHint = "Hydrothermal vents support sunlight-independent chemosynthetic ecosystems powered by sulfide-oxidizing microbes, offering astrobiologists insights into potential life beneath icy moons like Europa and Enceladus."
        ),
        PracticeTaskItem(
            id = "pte_essay",
            examType = ExamType.PTE,
            skill = SkillCategory.WRITING,
            title = "Write Essay",
            subtitle = "200–300 word academic argumentative essay",
            questionCount = 48,
            creditCost = 1,
            creditPool = CreditPoolType.ESSAY,
            aiScored = true,
            mode = TrainerInteractionMode.WRITING_TEXT,
            timeLimitSeconds = 1200,
            promptText = "Write a 200–300 word essay discussing the statement below. Support your view with clear examples:",
            passageOrTranscript = "Remote and hybrid work models improve overall productivity and employee wellbeing, yet some organizations argue they weaken mentorship and innovation. To what extent do you agree or disagree?",
            modelAnswerHint = "Use a 4-paragraph structure: crisp thesis intro, two balanced body paragraphs with concrete examples, and a decisive conclusion."
        ),
        PracticeTaskItem(
            id = "pte_sst",
            examType = ExamType.PTE,
            skill = SkillCategory.WRITING,
            title = "Summarize Spoken Text",
            subtitle = "Listen to the lecture and write 50–70 words",
            questionCount = 30,
            creditCost = 1,
            creditPool = CreditPoolType.SST,
            aiScored = true,
            mode = TrainerInteractionMode.WRITING_TEXT,
            timeLimitSeconds = 600,
            promptText = "Listen to the audio lecture and summarize the key points in 50–70 words:",
            passageOrTranscript = "Biomimicry engineers design quieter high-speed trains by studying the kingfisher's streamlined beak. When the bird dives from air into water, its beak prevents splashing; similarly, reshaping train noses eliminated sonic booms at tunnel exits while cutting energy usage by 15%.",
            modelAnswerHint = "Focus on biomimicry, the kingfisher beak's splash-free geometry, and how reshaping high-speed train noses eliminated tunnel sonic booms and reduced energy consumption by 15%."
        ),
        PracticeTaskItem(
            id = "pte_wfd",
            examType = ExamType.PTE,
            skill = SkillCategory.WRITING,
            title = "Write From Dictation",
            subtitle = "High-weight listening-to-writing accuracy drill",
            questionCount = 90,
            creditCost = 1,
            creditPool = CreditPoolType.UNIVERSAL,
            aiScored = true,
            mode = TrainerInteractionMode.WRITING_TEXT,
            timeLimitSeconds = 90,
            promptText = "Play the audio dictation and type the exact sentence with accurate spelling and punctuation:",
            passageOrTranscript = "Climate resilience requires coordinated investment across coastal infrastructure and renewable energy grids.",
            modelAnswerHint = "Climate resilience requires coordinated investment across coastal infrastructure and renewable energy grids."
        ),

        // PTE Reading
        PracticeTaskItem(
            id = "pte_rw_fib",
            examType = ExamType.PTE,
            skill = SkillCategory.READING,
            title = "R&W Fill in the Blanks",
            subtitle = "Context-driven academic collocation & grammar",
            questionCount = 54,
            creditCost = 0,
            creditPool = CreditPoolType.UNIVERSAL,
            aiScored = false,
            mode = TrainerInteractionMode.READING_DROPDOWN,
            timeLimitSeconds = 180,
            promptText = "Select the most accurate academic word to complete the passage:",
            passageOrTranscript = "Longitudinal studies demonstrate that early bilingual exposure [BLANK_1] executive control, enabling learners to switch between cognitive tasks with remarkable [BLANK_2].",
            options = listOf("enhances / agility", "declines / rigidity", "ignores / delay", "restricts / friction"),
            correctIndices = setOf(0),
            modelAnswerHint = "'enhances' collocates with 'executive control', and 'agility' pairs with 'remarkable'."
        ),
        PracticeTaskItem(
            id = "pte_r_fib",
            examType = ExamType.PTE,
            skill = SkillCategory.READING,
            title = "Reading Fill in the Blanks",
            subtitle = "Tap word bank chips into passage blanks",
            questionCount = 46,
            creditCost = 0,
            creditPool = CreditPoolType.UNIVERSAL,
            aiScored = false,
            mode = TrainerInteractionMode.READING_DRAG_BLANKS,
            timeLimitSeconds = 180,
            promptText = "Choose the best word from the bank to fill the blank:",
            passageOrTranscript = "Because archival parchment is sensitive to humidity, museum curators store manuscripts inside [____] climate vaults.",
            wordBank = listOf("stabilized", "volatile", "fragmented", "unsealed"),
            correctIndices = setOf(0),
            modelAnswerHint = "stabilized"
        ),
        PracticeTaskItem(
            id = "pte_r_reorder",
            examType = ExamType.PTE,
            skill = SkillCategory.READING,
            title = "Re-order Paragraphs",
            subtitle = "Arrange independent sentences into logical flow",
            questionCount = 32,
            creditCost = 0,
            creditPool = CreditPoolType.UNIVERSAL,
            aiScored = false,
            mode = TrainerInteractionMode.REORDER_PARAGRAPHS,
            timeLimitSeconds = 180,
            promptText = "Move paragraphs up or down to form the coherent academic sequence:",
            reorderItems = listOf(
                "However, scaling solid-state batteries for commercial aviation remains constrained by manufacturing cost.",
                "Lithium-ion cells transformed portable electronics during the past three decades.",
                "Consequently, aerospace researchers are testing hybrid silicon-anode prototypes to bridge this gap.",
                "Despite their reliability, conventional liquid electrolytes pose thermal runaway risks at high energy density."
            ),
            modelAnswerHint = "Correct order: Lithium-ion history -> Despite reliability (liquid risks) -> However, scaling solid-state -> Consequently, aerospace researchers..."
        ),
        PracticeTaskItem(
            id = "pte_r_mcq_single",
            examType = ExamType.PTE,
            skill = SkillCategory.READING,
            title = "Reading MCQ (Single)",
            subtitle = "Identify the primary claim in the passage",
            questionCount = 25,
            creditCost = 0,
            creditPool = CreditPoolType.UNIVERSAL,
            aiScored = false,
            mode = TrainerInteractionMode.MCQ_SINGLE,
            timeLimitSeconds = 150,
            promptText = "What is the author's central argument regarding circadian lighting?",
            passageOrTranscript = "Hospitals replacing static fluorescent tubes with tunable circadian LEDs observed 18% shorter post-operative recovery stays, primarily because daytime blue-enriched wavelengths stabilized patient melatonin cycles.",
            options = listOf(
                "Tunable lighting speeds patient recovery by aligning biological melatonin rhythms.",
                "Fluorescent lamps are more cost-effective than LED arrays in clinics.",
                "Blue light should be maximized throughout nighttime sleep hours.",
                "Recovery duration depends exclusively on surgical medication."
            ),
            correctIndices = setOf(0),
            modelAnswerHint = "Option A directly matches the 18% shorter stay and melatonin stabilization evidence."
        ),
        PracticeTaskItem(
            id = "pte_r_mcq_multi",
            examType = ExamType.PTE,
            skill = SkillCategory.READING,
            title = "Reading MCQ (Multiple)",
            subtitle = "Select all factually supported statements",
            questionCount = 22,
            creditCost = 0,
            creditPool = CreditPoolType.UNIVERSAL,
            aiScored = false,
            mode = TrainerInteractionMode.MCQ_MULTIPLE,
            timeLimitSeconds = 180,
            promptText = "Which TWO conclusions are supported by the oceanographic report?",
            passageOrTranscript = "Autonomous Argo floats measure salinity and temperature down to 2,000 meters across all ocean basins, transmitting real-time profiles via satellite while revealing accelerated warming in deep Southern Ocean currents.",
            options = listOf(
                "Argo floats monitor temperature and salinity up to 2,000 meters deep.",
                "Satellite relays transmit ocean profiles in real time.",
                "Deep Southern Ocean currents are cooling rapidly.",
                "Human divers manually collect data from every Argo float weekly."
            ),
            correctIndices = setOf(0, 1),
            modelAnswerHint = "First two options match the text; the report states warming (not cooling) and satellite relays (not divers)."
        ),

        // PTE Listening
        PracticeTaskItem(
            id = "pte_l_hcs",
            examType = ExamType.PTE,
            skill = SkillCategory.LISTENING,
            title = "Highlight Correct Summary",
            subtitle = "Pick the summary that accurately captures the audio",
            questionCount = 24,
            creditCost = 0,
            creditPool = CreditPoolType.UNIVERSAL,
            aiScored = false,
            mode = TrainerInteractionMode.LISTENING_AUDIO,
            timeLimitSeconds = 150,
            promptText = "Play the lecture audio and choose the single best summary:",
            passageOrTranscript = "Ancient Polynesian navigators crossed thousands of kilometers of open Pacific Ocean without magnetic compasses, reading swell interference patterns, star paths, and seabird flight trajectories.",
            options = listOf(
                "Polynesian wayfinders navigated vast Pacific routes using ocean swells, stars, and bird behavior.",
                "Magnetic compasses enabled early Polynesian voyages across shallow coastal lagoons.",
                "Pacific navigation relied strictly on written maps traded from Mediterranean sailors."
            ),
            correctIndices = setOf(0),
            modelAnswerHint = "Option 1 matches swell patterns, star paths, and seabirds."
        ),
        PracticeTaskItem(
            id = "pte_l_smw",
            examType = ExamType.PTE,
            skill = SkillCategory.LISTENING,
            title = "Select Missing Word",
            subtitle = "Predict the final muted phrase from context",
            questionCount = 26,
            creditCost = 0,
            creditPool = CreditPoolType.UNIVERSAL,
            aiScored = false,
            mode = TrainerInteractionMode.LISTENING_AUDIO,
            timeLimitSeconds = 120,
            promptText = "Listen to the audio excerpt and select the word/phrase that completes the final beep:",
            passageOrTranscript = "Before launching any clinical vaccine trial, independent ethics committees review participant consent protocols to safeguard [BEEP].",
            options = listOf("patient welfare", "ticket revenue", "weather forecasts", "highway speed"),
            correctIndices = setOf(0),
            modelAnswerHint = "'patient welfare' logically completes 'safeguard' in a clinical ethics context."
        ),

        // IELTS Segment Tasks (Cambridge 11-21 + Skimming + AI Writing + Speaking)
        PracticeTaskItem(
            id = "ielts_l_c21",
            examType = ExamType.IELTS,
            skill = SkillCategory.LISTENING,
            title = "Cambridge 21 Listening",
            subtitle = "Tests 1–4 · Section 1–4 Academic & Everyday Audio",
            questionCount = 16,
            creditCost = 0,
            creditPool = CreditPoolType.UNIVERSAL,
            aiScored = false,
            mode = TrainerInteractionMode.LISTENING_AUDIO,
            timeLimitSeconds = 300,
            promptText = "Cambridge 21 · Test 1 Section 3: Listen to the two students discussing their marine acoustics fieldwork:",
            passageOrTranscript = "Liam notes that hydrophone placement near the shipping lane introduced low-frequency engine noise, so Maya recommends relocating sensors 400 meters west toward the seagrass reef.",
            options = listOf(
                "Relocate hydrophones 400m west near the seagrass reef to reduce shipping noise.",
                "Cancel the acoustic survey due to high tides.",
                "Move sensors directly inside the commercial shipping channel."
            ),
            correctIndices = setOf(0),
            modelAnswerHint = "Maya explicitly recommends moving 400 meters west toward the seagrass reef."
        ),
        PracticeTaskItem(
            id = "ielts_r_skim",
            examType = ExamType.IELTS,
            skill = SkillCategory.READING,
            title = "Cambridge 11–21 + Skimming Trainer",
            subtitle = "Timed speed-skimming & True/False/Not Given",
            questionCount = 44,
            creditCost = 0,
            creditPool = CreditPoolType.UNIVERSAL,
            aiScored = false,
            mode = TrainerInteractionMode.MCQ_SINGLE,
            timeLimitSeconds = 240,
            promptText = "Cambridge 21 Reading · Skim for heading match in under 60s:",
            passageOrTranscript = "While medieval guilds tightly guarded glassmaking formulas on Murano Island, Venetian merchants exported mirrors across Europe, transforming optical science and portrait art alike.",
            options = listOf(
                "TRUE — Venetian mirrors influenced both optics and European art.",
                "FALSE — Murano guilds openly published all glassmaking formulas.",
                "NOT GIVEN — Exact mirror pricing in 14th-century Paris."
            ),
            correctIndices = setOf(0),
            modelAnswerHint = "TRUE: the passage directly states Venetian mirrors transformed optical science and portrait art."
        ),
        PracticeTaskItem(
            id = "ielts_w_t2",
            examType = ExamType.IELTS,
            skill = SkillCategory.WRITING,
            title = "Cambridge 21 AI Essay Practice",
            subtitle = "Task 1 & Task 2 with live Band 0–9 AI feedback",
            questionCount = 24,
            creditCost = 1,
            creditPool = CreditPoolType.IELTS_ESSAY,
            aiScored = true,
            mode = TrainerInteractionMode.WRITING_TEXT,
            timeLimitSeconds = 2400,
            promptText = "IELTS Writing Task 2 (minimum 250 words recommended):",
            passageOrTranscript = "Some cities are banning private petrol cars from historic centers and investing in free electric transit. Do the advantages of this policy outweigh the disadvantages?",
            modelAnswerHint = "Address both advantages (air quality, pedestrian safety) and drawbacks (commuter transition friction), clearly stating your position for Band 8+."
        ),
        PracticeTaskItem(
            id = "ielts_s_part2",
            examType = ExamType.IELTS,
            skill = SkillCategory.SPEAKING,
            title = "IELTS Speaking Cue Card",
            subtitle = "Part 1–3 interactive voice practice + AI band score",
            questionCount = 30,
            creditCost = 1,
            creditPool = CreditPoolType.SPEAKING,
            aiScored = true,
            mode = TrainerInteractionMode.SPEAKING_MIC,
            timeLimitSeconds = 120,
            prepSeconds = 15,
            promptText = "IELTS Speaking Part 2 Cue Card — Speak for 1 to 2 minutes:",
            passageOrTranscript = "Describe a skill you learned online that significantly helped your studies or career. Say: what the skill was, how you practiced it, challenges you overcame, and why it remains valuable.",
            modelAnswerHint = "Cover all four bullet points smoothly using discourse markers ('initially', 'what struck me most', 'looking back')."
        )
    )

    val mockTests: List<MockTestItem> = listOf(
        MockTestItem(
            id = "pte_mock_1",
            examType = ExamType.PTE,
            title = "PTE Practice Test 1",
            subtitle = "Core Speaking & Writing benchmark",
            durationMinutes = 65,
            sections = listOf("Speaking", "Writing"),
            creditCost = 1,
            creditPool = CreditPoolType.MOCK,
            priceLkr = 1500,
            unlocked = true
        ),
        MockTestItem(
            id = "pte_mock_2",
            examType = ExamType.PTE,
            title = "PTE Practice Test 2",
            subtitle = "Reading & Listening timed simulation",
            durationMinutes = 70,
            sections = listOf("Reading", "Listening"),
            creditCost = 1,
            creditPool = CreditPoolType.MOCK,
            priceLkr = 1500,
            unlocked = true
        ),
        MockTestItem(
            id = "pte_full_1",
            examType = ExamType.PTE,
            title = "PTE Full Mock Exam 1",
            subtitle = "Official 2-hour Pearson format + AI report",
            durationMinutes = 120,
            sections = listOf("Speaking", "Writing", "Reading", "Listening"),
            creditCost = 1,
            creditPool = CreditPoolType.MOCK,
            priceLkr = 2900,
            unlocked = true
        ),
        MockTestItem(
            id = "pte_full_2",
            examType = ExamType.PTE,
            title = "PTE Full Mock Exam 2",
            subtitle = "High-frequency predicted bank + full scorecard",
            durationMinutes = 120,
            sections = listOf("Speaking", "Writing", "Reading", "Listening"),
            creditCost = 2,
            creditPool = CreditPoolType.MOCK,
            priceLkr = 3500,
            unlocked = false
        ),
        MockTestItem(
            id = "ielts_mock_1",
            examType = ExamType.IELTS,
            title = "IELTS Academic Mock 1",
            subtitle = "Listening + Reading + Writing (Band Average)",
            durationMinutes = 160,
            sections = listOf("Listening", "Reading", "Writing"),
            creditCost = 1,
            creditPool = CreditPoolType.IELTS_MOCK,
            priceLkr = 2500,
            unlocked = true
        ),
        MockTestItem(
            id = "ielts_mock_2",
            examType = ExamType.IELTS,
            title = "IELTS Academic Mock 2",
            subtitle = "Cambridge 21 calibrated full band simulation",
            durationMinutes = 160,
            sections = listOf("Listening", "Reading", "Writing"),
            creditCost = 1,
            creditPool = CreditPoolType.IELTS_MOCK,
            priceLkr = 2500,
            unlocked = true
        ),
        MockTestItem(
            id = "level_test",
            examType = ExamType.PTE,
            title = "30-Min Level Placement Test",
            subtitle = "Instant baseline CEFR & target gap report",
            durationMinutes = 30,
            sections = listOf("Speaking", "Writing", "Reading"),
            creditCost = 0,
            creditPool = CreditPoolType.UNIVERSAL,
            priceLkr = 0,
            unlocked = true,
            isLevelTest = true
        )
    )

    val courses: List<CourseItem> = listOf(
        CourseItem(
            id = "course_pte_79",
            title = "PTE 79+ Intensive Masterclass",
            lecturer = "Dr. Kavinda Jayawardena",
            examType = ExamType.PTE,
            schedule = "Tue & Thu · 7:30 PM IST",
            priceLkr = 24500,
            syllabus = listOf("Speaking Fluency & Pitch Tuning", "SWT & Essay Templates", "WFD High-Yield Drills", "Full Mock Deconstruction"),
            rating = 4.9,
            enrolledCount = 412
        ),
        CourseItem(
            id = "course_ielts_8",
            title = "IELTS Band 8.0 Studio Cohort",
            lecturer = "Amaya Wickramasinghe",
            examType = ExamType.IELTS,
            schedule = "Sat & Sun · 10:00 AM IST",
            priceLkr = 28000,
            priceUsd = 95,
            syllabus = listOf("Cambridge 20–21 Reading Speed", "Task 1 Visual Precision", "Task 2 Cohesive Arguments", "Live Speaking Mock Panels"),
            rating = 4.9,
            enrolledCount = 318
        ),
        CourseItem(
            id = "course_grammar",
            title = "Grammar Clinic & Feedback Club",
            lecturer = "Nadeesha Fernando",
            examType = ExamType.PTE,
            schedule = "Wed · 8:00 PM IST",
            priceLkr = 12000,
            syllabus = listOf("Complex Clauses", "Academic Collocations", "Error-Free Tense Control"),
            rating = 4.8,
            enrolledCount = 196
        )
    )

    val recordedPackages: List<RecordedPackageItem> = listOf(
        RecordedPackageItem(
            id = "rec_pte_vault",
            title = "PTE 2026 Recorded Strategy Vault",
            lecturer = "Dr. Kavinda Jayawardena",
            durationHours = "18.5 hrs",
            lessonsCount = 24,
            examType = ExamType.PTE,
            lessons = listOf(
                "01 · Oral Fluency Microphone Calibration",
                "02 · Repeat Sentence Memory Chunking",
                "03 · Describe Image 30-Second Framework",
                "04 · Write Essay 90-Score Blueprint"
            )
        ),
        RecordedPackageItem(
            id = "rec_ielts_vault",
            title = "IELTS Cambridge 21 Walkthroughs",
            lecturer = "Amaya Wickramasinghe",
            durationHours = "14.0 hrs",
            lessonsCount = 18,
            examType = ExamType.IELTS,
            lessons = listOf(
                "01 · Skimming & Scanning Under 60s",
                "02 · True / False / Not Given Logic",
                "03 · Band 8.5 Task 2 Cohesion",
                "04 · Part 3 Abstract Follow-Ups"
            )
        )
    )

    val lecturers: List<LecturerProfile> = listOf(
        LecturerProfile(
            id = "lec_kavinda",
            name = "Dr. Kavinda Jayawardena",
            title = "Lead PTE 90/90 Strategist",
            subjects = listOf("PTE Speaking", "PTE Writing", "Mock Review"),
            ratePerHourLkr = 4500,
            supportsOnline = true,
            supportsPhysical = true,
            rating = 4.95,
            bio = "90/90 scorer with 8+ years coaching medical & engineering migrants."
        ),
        LecturerProfile(
            id = "lec_amaya",
            name = "Amaya Wickramasinghe",
            title = "Senior IELTS Band 9 Examiner Coach",
            subjects = listOf("IELTS Writing", "IELTS Speaking", "Reading Skimming"),
            ratePerHourLkr = 5000,
            supportsOnline = true,
            supportsPhysical = false,
            rating = 4.92,
            bio = "Specializes in Academic Writing Task 2 coherence & Band 8+ speaking."
        ),
        LecturerProfile(
            id = "lec_nadeesha",
            name = "Nadeesha Fernando",
            title = "Pronunciation & Grammar Specialist",
            subjects = listOf("Read Aloud", "Grammar Clinic", "WFD Accuracy"),
            ratePerHourLkr = 3800,
            supportsOnline = true,
            supportsPhysical = true,
            rating = 4.88,
            bio = "Acoustic phonetics trainer focused on clarity and pacing."
        )
    )

    val events: List<EventItem> = listOf(
        EventItem(
            id = "web_1",
            type = "Webinar",
            title = "2026 PTE Scoring Algorithm Updates",
            host = "Dr. Kavinda Jayawardena",
            dateTimeLabel = "Tomorrow · 7:30 PM IST",
            seatsLeft = 18,
            priceLkr = 0
        ),
        EventItem(
            id = "wk_1",
            type = "Workshop",
            title = "Live Essay & SWT Clinic (Hands-on)",
            host = "Amaya Wickramasinghe",
            dateTimeLabel = "Sat · 2:00 PM IST",
            seatsLeft = 9,
            priceLkr = 1800
        )
    )

    val resources: List<ResourceItem> = listOf(
        ResourceItem(
            id = "res_essays_2026",
            title = "Predicted PTE Essay Topics 2026",
            category = "PDF Guide",
            summary = "Top 45 high-frequency essay prompts with Band/90 outlines & vocabulary banks.",
            examType = ExamType.PTE
        ),
        ResourceItem(
            id = "res_ielts_c21",
            title = "Cambridge 21 Academic Wordlist",
            category = "Workshop Doc",
            summary = "Collocations and paraphrasing pairs extracted from Cambridge 21 Reading passages.",
            examType = ExamType.IELTS
        ),
        ResourceItem(
            id = "res_blog_1",
            title = "How 15-Minute Daily WFD Drills Add +9 Listening Points",
            category = "Blog",
            summary = "Evidence-backed spaced repetition technique for Write From Dictation mastery.",
            examType = ExamType.PTE
        )
    )

    val creditBundles: List<CreditBundle> = listOf(
        CreditBundle("cb_speak_10", "10 Speaking AI Credits", CreditPoolType.SPEAKING, 10L, 1900, "Popular"),
        CreditBundle("cb_essay_10", "10 PTE Essay AI Credits", CreditPoolType.ESSAY, 10L, 2200, "Best Value"),
        CreditBundle("cb_ielts_w", "8 IELTS Essay Credits", CreditPoolType.IELTS_ESSAY, 8L, 2400, "Band 8+"),
        CreditBundle("cb_mock_3", "3 Full PTE Mock Credits", CreditPoolType.MOCK, 3L, 6900, "Save 20%"),
        CreditBundle("cb_uni_25", "25 Universal Smart Credits", CreditPoolType.UNIVERSAL, 25L, 4500, "All Tasks")
    )

    // 15-minute step time slots with 15-minute break rule enforced
    val fifteenMinuteTimeSlots: List<String> = listOf(
        "09:00 AM", "09:15 AM", "09:30 AM", "09:45 AM",
        "10:15 AM", "10:30 AM", "11:00 AM", "11:30 AM",
        "02:00 PM", "02:15 PM", "02:30 PM", "03:00 PM",
        "04:15 PM", "04:30 PM", "06:00 PM", "06:30 PM",
        "07:00 PM", "07:15 PM", "07:45 PM", "08:30 PM"
    )
}
