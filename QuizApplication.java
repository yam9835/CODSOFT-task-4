import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

class Question {
    private String questionText;
    private ArrayList<String> options;
    private int correctOptionIndex;

    public Question(String questionText, ArrayList<String> options, int correctOptionIndex) {
        this.questionText = questionText;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
    }

    public String getQuestionText() {
        return questionText;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }
}

class Quiz {
    private ArrayList<Question> questions;
    private int currentQuestionIndex;
    private int userScore;
    private Timer timer;
    private boolean answerSubmitted;

    public Quiz(ArrayList<Question> questions) {
        this.questions = questions;
        this.currentQuestionIndex = 0;
        this.userScore = 0;
        this.timer = new Timer();
        this.answerSubmitted = false;
    }

    public void startQuiz() {
        for (Question question : questions) {
            displayQuestion(question);
            startTimer();
            waitForAnswer();
            stopTimer();

            if (answerSubmitted) {
                System.out.println("Correct!\n");
                userScore++;
            } else {
                System.out.println("Time's up! The correct answer was option " + (question.getCorrectOptionIndex() + 1) + "\n");
            }

            answerSubmitted = false;
        }

        displayResult();
    }

    private void displayQuestion(Question question) {
        System.out.println(question.getQuestionText());
        ArrayList<String> options = question.getOptions();
        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + ". " + options.get(i));
        }
    }

    private void startTimer() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Time's up!");
                answerSubmitted = false;
                synchronized (this) {
                    this.notify();
                }
            }
        }, 10000); // 10 seconds timer for each question
    }

    private void waitForAnswer() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your answer (1-" + questions.get(currentQuestionIndex).getOptions().size() + "): ");

        try {
            int userAnswer = scanner.nextInt();
            if (userAnswer >= 1 && userAnswer <= questions.get(currentQuestionIndex).getOptions().size()) {
                checkAnswer(userAnswer);
            } else {
                System.out.println("Invalid input. Please enter a valid option.");
                waitForAnswer();
            }
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a valid option.");
            waitForAnswer();
        }
    }

    private void checkAnswer(int userAnswer) {
        if (userAnswer - 1 == questions.get(currentQuestionIndex).getCorrectOptionIndex()) {
            answerSubmitted = true;
        }
        synchronized (timer) {
            timer.notify();
        }
    }

    private void stopTimer() {
        timer.cancel();
        timer = new Timer();
    }

    private void displayResult() {
        System.out.println("Quiz completed!");
        System.out.println("Your score: " + userScore + " out of " + questions.size());
    }
}

public class QuizApplication {
    public static void main(String[] args) {
        // Sample quiz questions
        ArrayList<Question> questions = new ArrayList<>();
        questions.add(new Question("What is the capital of japan?", new ArrayList<>(List.of("Berlin", "Tokyo", "Seoul")), 1));
        questions.add(new Question("Who wrote "Romeo and Juliet"?", new ArrayList<>(List.of("William Shakespeare","Jane Austen","Charles Dickens")), 0));
        questions.add(new Question("What is the largest ocean on Earth?", new ArrayList<>(List.of("Atlantic Ocean", "Indian Ocean", "Pacific Ocean")), 2));

       
        Quiz quiz = new Quiz(questions);

      
        quiz.startQuiz();
    }
}
