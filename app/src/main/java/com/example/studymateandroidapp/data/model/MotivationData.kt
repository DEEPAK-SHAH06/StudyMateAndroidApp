package com.example.studymateandroidapp.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Daily reflection journal entry.
 */
@Entity(
    tableName = "daily_reflections",
    indices = [Index("date", unique = true)]
)
data class DailyReflection(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long = 0L, // epoch day
    val content: String = "",
    val mood: String = "😊", // emoji mood
    val studyHighlight: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val userId: String = "",
    val serverId: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)

/**
 * Achievement badge unlocked by the user.
 */
@Entity(
    tableName = "achievements",
    indices = [Index("type", unique = true)]
)
data class Achievement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: AchievementType = AchievementType.FIRST_TASK,
    val unlockedAt: Long = System.currentTimeMillis(),
    val title: String = "",
    val description: String = "",
    val userId: String = "",
    val serverId: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)

/**
 * Types of achievements that can be unlocked.
 */
enum class AchievementType {
    FIRST_TASK,
    TEN_TASKS,
    FIFTY_TASKS,
    FIRST_NOTE,
    TEN_NOTES,
    FIRST_GOAL_COMPLETE,
    FIVE_GOALS_COMPLETE,
    STUDY_HOUR,
    STUDY_TEN_HOURS,
    SEVEN_DAY_STREAK,
    FOURTEEN_DAY_STREAK,
    THIRTY_DAY_STREAK,
    FIRST_FLASHCARD,
    FIRST_REFLECTION,
    POMODORO_MASTER,      // Complete 10 full pomodoros
    POMODORO_LEGEND,       // Complete 50 full pomodoros
    STREAK_3_DAY,         // First Spark
    STREAK_7_DAY,         // Consistent Learner
    STREAK_30_DAY,        // Study Warrior
    STREAK_100_DAY,       // Unstoppable
    NIGHT_OWL,           // Study after 12:00 AM
    EARLY_BIRD,          // Study before 7:00 AM
    FLASHCARD_MASTER,    // Create 50 flashcards
    CONSISTENCY_KING,    // Maintain 14-day streak
    MARATHON_STUDIER     // Study 100 total hours
}

/**
 * Hardcoded offline motivational quotes.
 */
object MotivationalQuotes {
    val quotes = listOf(
        "The secret of getting ahead is getting started." to "Mark Twain",
        "It does not matter how slowly you go as long as you do not stop." to "Confucius",
        "Education is the most powerful weapon which you can use to change the world." to "Nelson Mandela",
        "The only way to do great work is to love what you do." to "Steve Jobs",
        "Success is not final, failure is not fatal: it is the courage to continue that counts." to "Winston Churchill",
        "The future belongs to those who believe in the beauty of their dreams." to "Eleanor Roosevelt",
        "In the middle of difficulty lies opportunity." to "Albert Einstein",
        "The mind is not a vessel to be filled, but a fire to be kindled." to "Plutarch",
        "Learning never exhausts the mind." to "Leonardo da Vinci",
        "The beautiful thing about learning is that nobody can take it away from you." to "B.B. King",
        "Don't let what you cannot do interfere with what you can do." to "John Wooden",
        "The expert in anything was once a beginner." to "Helen Hayes",
        "Start where you are. Use what you have. Do what you can." to "Arthur Ashe",
        "The only person you are destined to become is the person you decide to be." to "Ralph Waldo Emerson",
        "Believe you can and you're halfway there." to "Theodore Roosevelt",
        "A journey of a thousand miles begins with a single step." to "Lao Tzu",
        "What we learn with pleasure we never forget." to "Alfred Mercier",
        "The more that you read, the more things you will know." to "Dr. Seuss",
        "Self-education is, I firmly believe, the only kind of education there is." to "Isaac Asimov",
        "Live as if you were to die tomorrow. Learn as if you were to live forever." to "Mahatma Gandhi",
        "The capacity to learn is a gift; the ability to learn is a skill; the willingness to learn is a choice." to "Brian Herbert",
        "Education is not the filling of a pail, but the lighting of a fire." to "William Butler Yeats",
        "You don't have to be great to start, but you have to start to be great." to "Zig Ziglar",
        "The roots of education are bitter, but the fruit is sweet." to "Aristotle",
        "An investment in knowledge pays the best interest." to "Benjamin Franklin",
        "The only limit to our realization of tomorrow is our doubts of today." to "Franklin D. Roosevelt",
        "Intelligence plus character — that is the goal of true education." to "Martin Luther King Jr.",
        "Genius is one percent inspiration and ninety-nine percent perspiration." to "Thomas Edison",
        "Tell me and I forget. Teach me and I remember. Involve me and I learn." to "Benjamin Franklin",
        "The best time to plant a tree was 20 years ago. The second best time is now." to "Chinese Proverb",
        "Knowledge is power." to "Francis Bacon",
        "Study hard what interests you the most in the most undisciplined way." to "Richard Feynman",
        "The more I read, the more I acquire, the more certain I am that I know nothing." to "Voltaire",
        "Success usually comes to those who are too busy to be looking for it." to "Henry David Thoreau",
        "Do what you can, with what you have, where you are." to "Theodore Roosevelt",
        "It always seems impossible until it's done." to "Nelson Mandela",
        "The only source of knowledge is experience." to "Albert Einstein",
        "Perseverance is not a long race; it is many short races one after the other." to "Walter Elliot",
        "Quality is not an act, it is a habit." to "Aristotle",
        "Small daily improvements over time lead to stunning results." to "Robin Sharma",
        "Your limitation — it's only your imagination." to "Unknown",
        "Push yourself, because no one else is going to do it for you." to "Unknown",
        "Great things never come from comfort zones." to "Unknown",
        "Dream it. Wish it. Do it." to "Unknown",
        "Success doesn't just find you. You have to go out and get it." to "Unknown",
        "The harder you work for something, the greater you'll feel when you achieve it." to "Unknown",
        "Dream bigger. Do bigger." to "Unknown",
        "Don't stop when you're tired. Stop when you're done." to "Unknown",
        "Wake up with determination. Go to bed with satisfaction." to "Unknown",
        "Do something today that your future self will thank you for." to "Unknown",
        "Little things make big days." to "Unknown",
        "It's going to be hard, but hard does not mean impossible." to "Unknown",
        "Don't wait for opportunity. Create it." to "Unknown",
        "Sometimes we're tested not to show our weaknesses, but to discover our strengths." to "Unknown",
        "The key to success is to focus on goals, not obstacles." to "Unknown",
        "Discipline is the bridge between goals and accomplishment." to "Jim Rohn",
        "Motivation gets you going, but discipline keeps you growing." to "John C. Maxwell",
        "The pain of discipline is nothing like the pain of disappointment." to "Justin Langer",
        "Study while others are sleeping; decide while others are delaying." to "William Arthur Ward",
        "There are no shortcuts to any place worth going." to "Beverly Sills",
        "The difference between ordinary and extraordinary is that little extra." to "Jimmy Johnson",
        "Education is the passport to the future." to "Malcolm X",
        "Don't count the days, make the days count." to "Muhammad Ali",
        "You are never too old to set another goal or to dream a new dream." to "C.S. Lewis",
        "Strive for progress, not perfection." to "Unknown",
        "The way to get started is to quit talking and begin doing." to "Walt Disney",
        "Doubt kills more dreams than failure ever will." to "Suzy Kassem",
        "Hard work beats talent when talent doesn't work hard." to "Tim Notke",
        "Every accomplishment starts with the decision to try." to "John F. Kennedy",
        "You miss 100% of the shots you don't take." to "Wayne Gretzky",
        "What you do today can improve all your tomorrows." to "Ralph Marston",
        "Learn from yesterday, live for today, hope for tomorrow." to "Albert Einstein",
        "The only way to learn mathematics is to do mathematics." to "Paul Halmos",
        "Reading is to the mind what exercise is to the body." to "Joseph Addison",
        "Develop a passion for learning. If you do, you will never cease to grow." to "Anthony J. D'Angelo",
        "Knowledge speaks, but wisdom listens." to "Jimi Hendrix",
        "The greatest glory in living lies not in never falling, but in rising every time we fall." to "Nelson Mandela",
        "Your education is a dress rehearsal for a life that is yours to lead." to "Nora Ephron",
        "The writer does the greatest good who gives the reader the most knowledge." to "Sydney Smith",
        "To learn, you need a certain degree of confidence." to "Haruki Murakami",
        "Curiosity is the wick in the candle of learning." to "William Arthur Ward",
        "I am still learning." to "Michelangelo",
        "Every student can learn, just not on the same day, or in the same way." to "George Evans",
        "The world is a book and those who do not travel read only one page." to "Augustine of Hippo",
        "Learning is a treasure that will follow its owner everywhere." to "Chinese Proverb",
        "Change is the end result of all true learning." to "Leo Buscaglia",
        "The more you know, the more you realize you don't know." to "Aristotle",
        "To teach is to learn twice over." to "Joseph Joubert",
        "Intellectual growth should commence at birth and cease only at death." to "Albert Einstein",
        "The learned man knows that he is ignorant." to "Victor Hugo",
        "If you think education is expensive, try ignorance." to "Derek Bok",
        "Once you stop learning, you start dying." to "Albert Einstein",
        "Never let formal education get in the way of your learning." to "Mark Twain",
        "The purpose of learning is growth, and our minds, unlike our bodies, can continue growing." to "Mortimer Adler",
        "Anyone who stops learning is old, whether at twenty or eighty." to "Henry Ford",
        "Study without desire spoils the memory." to "Leonardo da Vinci",
        "A wise man can learn more from a foolish question than a fool can learn from a wise answer." to "Bruce Lee",
        "Today a reader, tomorrow a leader." to "Margaret Fuller",
        "Stay hungry, stay foolish." to "Steve Jobs",
        "Success is the sum of small efforts, repeated day in and day out." to "Robert Collier"
    )

    fun getQuoteForDate(epochDay: Long): Pair<String, String> {
        val index = (epochDay % quotes.size).toInt().let { if (it < 0) it + quotes.size else it }
        return quotes[index]
    }
}
