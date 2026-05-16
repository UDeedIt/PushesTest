package pro.udeedit.devtools.pushestest.utils

data class MockNotification(
    val title: String,
    val body: String,
    val summary: String
)

object PtMockDataUtils {

    private val mockList = listOf(
        // SYSTEM & DEV
        MockNotification("System Update", "Your device is ready to install version 14.5.1-beta.", "System"),
        MockNotification("Database Error", "Critical: Connection timeout on node-04 cluster.", "Server Alerts"),
        MockNotification("New Pull Request", "Developer 'alex_v' opened a PR in repository 'pushes-test-app'.", "GitHub"),
        MockNotification("Build Failed", "Workflow 'Android CI' failed for commit #88f12a.", "CI/CD Pipeline"),
        MockNotification("Memory Warning", "App 'Game_Engine' is using more than 500MB of RAM.", "Monitoring"),
        MockNotification("Server Online", "Primary production server has successfully restarted.", "Status"),
        MockNotification("New User", "User #9982 has just registered using Google Auth.", "Analytics"),
        MockNotification("API 404", "Spike in 404 errors detected in /api/v1/auth endpoint.", "DevOps"),
        MockNotification("Docker Alert", "Container 'redis-cache' exited with code 137.", "Infrastructure"),
        MockNotification("SSH Login", "New SSH login detected from IP 192.168.1.50.", "Security"),

        // MESSAGING & SOCIAL
        MockNotification("Mom", "Don't forget to buy milk on your way home!", "Chat"),
        MockNotification("Slack: #general", "Alex: Does anyone have the meeting link for today?", "Slack"),
        MockNotification("Telegram", "You received a video file (45MB).", "Messenger"),
        MockNotification("WhatsApp", "New message from +49 152 0000000.", "WhatsApp"),
        MockNotification("Instagram", "User 'nature_pics' liked your story.", "Social"),
        MockNotification("LinkedIn", "Your profile appeared in 15 searches this week.", "Jobs"),
        MockNotification("Twitter", "Breaking news: New discoveries found on Mars.", "News"),
        MockNotification("Discord", "Someone mentioned you in #android-dev.", "Communities"),
        MockNotification("Email", "Subject: Important update regarding your subscription.", "Inbox"),
        MockNotification("Facebook", "It's Sarah's birthday today. Write on her timeline!", "Birthdays"),

        // FINTECH & TRANSACTIONS
        MockNotification("Bank Alert", "You spent $45.00 at 'Coffee Shop Berlin'.", "Transactions"),
        MockNotification("Crypto.com", "Bitcoin is up 5.4% in the last 24 hours.", "Markets"),
        MockNotification("PayPal", "You sent $150.00 to 'Freelance Designer'.", "Payments"),
        MockNotification("Low Balance", "Your account #4402 is below $10.00.", "Finance"),
        MockNotification("Stock Market", "AAPL hit a new 52-week high today.", "Investing"),
        MockNotification("Tax Office", "Your digital tax return is ready for review.", "Government"),
        MockNotification("Invoice Paid", "Invoice #2024-001 has been marked as paid.", "Business"),
        MockNotification("Refund Issued", "A refund of $12.99 has been sent to your card.", "Shopping"),
        MockNotification("Subscription", "Netflix: Your monthly plan will renew tomorrow.", "Bills"),
        MockNotification("Salary", "Your monthly salary has been deposited.", "Finance"),

        // LOGISTICS & TRAVEL
        MockNotification("Uber", "Your driver is 2 minutes away in a White Toyota.", "Travel"),
        MockNotification("Flight Update", "Flight LH204 to Munich is delayed by 45 minutes.", "Airlines"),
        MockNotification("Amazon", "Package out for delivery. Expected by 8 PM.", "Orders"),
        MockNotification("DHL", "Your shipment is ready for pickup at Station 104.", "Parcel"),
        MockNotification("Airbnb", "Host 'Marco' sent you a message about your stay.", "Booking"),
        MockNotification("Train Info", "Platform change: ICE 592 now departing from Platform 4.", "Railway"),
        MockNotification("Food Delivery", "Your order from 'Burger King' is being prepared.", "Lunch"),
        MockNotification("Hotel", "Check-in for 'Grand Hotel' starts at 3 PM.", "Travel"),
        MockNotification("E-scooter", "Ride completed. Total cost: 4.50€.", "Mobility"),
        MockNotification("Fuel Station", "Earn 5x points on your next fuel purchase.", "Rewards"),

        // HEALTH & LIFESTYLE
        MockNotification("Fitness App", "Time to stand up! You've been sitting for 60 minutes.", "Health"),
        MockNotification("Water Reminder", "You've only had 1L today. Drink some water!", "Wellness"),
        MockNotification("Workout Plan", "Leg day starts in 15 minutes. Get ready!", "Gym"),
        MockNotification("Sleep Cycle", "You slept 7h 45m last night. Quality: High.", "Sleep"),
        MockNotification("Meditation", "Take a 5-minute break to breathe and relax.", "Mindfulness"),
        MockNotification("Pharmacy", "Your prescription is ready for pickup.", "Health"),
        MockNotification("Doctor", "Appointment confirmed for Monday at 9:00 AM.", "Calendar"),
        MockNotification("Yoga", "New session 'Morning Flow' is now live.", "Lifestyle"),
        MockNotification("Recipe", "Try this new 15-minute pasta recipe today.", "Cooking"),
        MockNotification("Steps Goal", "Goal reached! You walked 10,000 steps today.", "Fitness"),

        // RANDOM & FUN
        MockNotification("Trivia Quiz", "What is the capital of Kazakhstan?", "Games"),
        MockNotification("Daily Quote", "The best way to predict the future is to create it.", "Motivation"),
        MockNotification("Weather", "Expect heavy rain at 5 PM. Bring an umbrella!", "Forecast"),
        MockNotification("NASA", "Stunning new images of Jupiter's Great Red Spot.", "Science"),
        MockNotification("Music Player", "Now playing: 'Midnight City' by M83.", "Entertainment"),
        MockNotification("Movie News", "New trailer for 'Dune 3' just dropped.", "Cinema"),
        MockNotification("App Store", "Your app 'Pushes Test' was approved!", "Console"),
        MockNotification("Lottery", "The jackpot is now 50 Million! Try your luck.", "Ads"),
        MockNotification("Battery", "15% remaining. Switch to Power Saving mode?", "System"),
        MockNotification("Wi-Fi", "Connected to 'Airport_Free_Wifi'.", "Network")
    )

    // Big Text
    private val bigTextMockList = listOf(
        MockNotification(
            "System Logs Analysis",
            "CRITICAL ERROR: Stack trace detected in module 'auth-service'. \n\nAt pro.udeedit.auth.Login(Login.kt:45)\nAt pro.udeedit.auth.Validator.check(Validator.kt:12)\nCaused by: NullPointerException at line 88. \n\nPlease review the deployment logs immediately to prevent further downtime.",
            "Developer Alerts"
        ),
        MockNotification(
            "Release Notes v2.4.0",
            "What's new in this version:\n• Added support for Android 15\n• Improved battery efficiency by 15%\n• Fixed a rare crash in the BottomSheet logic\n• Updated Inter font family weights\n• Refactored the notification dispatch engine for better performance on tablets.",
            "App Updates"
        ),
        MockNotification(
            "Terms of Service Update",
            "We have updated our privacy policy. By continuing to use the Pushes Test D2D utility, you agree to the new terms. Our commitment to your privacy remains our top priority. We do not collect or share any personal data from your notification payloads. Review the full document on our GitHub repository.",
            "Legal"
        ),
        MockNotification(
            "AI Assistant Summary",
            "Your meeting with the design team was productive. Key takeaways: 1. Finalize the primary blue color #146683. 2. Implement the Inter font globally. 3. Ensure the Full-Screen intent is working for Alarms. 4. Launch the beta version by Friday. Next meeting scheduled for Monday morning.",
            "Productivity"
        ),
        MockNotification(
            "Security Incident Report",
            "An unusual login attempt was blocked from an unrecognized IP address (192.168.1.254). The attempt originated from a device running Linux in Dublin, Ireland. If this wasn't you, please reset your administrative password immediately and enable two-factor authentication for the production cluster.",
            "Security"
        )
        // Note: You can duplicate these templates with different numbers/names to reach 100+ variations
    )


    // Total variations: Over 60. You can easily duplicate these
    // or slightly vary them to reach 100+ very quickly.

    fun getRandomMockData(): MockNotification {
        return mockList.random()
    }

    // New function for Big Text
    fun getRandomBigMockData(): MockNotification {
        return bigTextMockList.random()
    }

}

