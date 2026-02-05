interface ModerationRule {
    int apply(int marks);
}

class AttendanceModeration implements ModerationRule {
    public int apply(int marks) { return marks + 5; }
}

class DifficultyModeration implements ModerationRule {
    public int apply(int marks) { return marks + 10; }
}

class ManualModeration implements ModerationRule {
    public int apply(int marks) { return marks + 2; }
}

abstract class Evaluation {
    protected ModerationRule moderationRule;

    public Evaluation(ModerationRule moderationRule) {
        this.moderationRule = moderationRule;
    }

    public final void evaluate() {
        int theory = collectTheoryMarks();
        int lab = collectLabMarks();
        int total = calculateFinalScore(theory, lab);
        total = moderationRule.apply(total);
        generateGrade(total);
    }

    protected int collectTheoryMarks() { return 70; }
    protected int collectLabMarks() { return 30; }

    protected abstract int calculateFinalScore(int theory, int lab);
    protected abstract void generateGrade(int total);
}

class BTechEvaluation extends Evaluation {
    public BTechEvaluation(ModerationRule rule) { super(rule); }

    protected int calculateFinalScore(int theory, int lab) { return theory + lab; }

    protected void generateGrade(int total) {
        if (total >= 80)
            System.out.println("B.Tech Grade: A");
        else
            System.out.println("B.Tech Grade: B");
    }
}

class MCAEvaluation extends Evaluation {
    public MCAEvaluation(ModerationRule rule) { super(rule); }

    protected int calculateFinalScore(int theory, int lab) {
        return (theory * 60 / 100) + (lab * 40 / 100);
    }

    protected void generateGrade(int total) {
        if (total >= 75)
            System.out.println("MCA Grade: A");
        else
            System.out.println("MCA Grade: B");
    }
}

class PhDEvaluation extends Evaluation {
    public PhDEvaluation(ModerationRule rule) { super(rule); }

    protected int calculateFinalScore(int theory, int lab) { return theory; }

    protected void generateGrade(int total) {
        if (total >= 85)
            System.out.println("PhD Grade: Pass");
        else
            System.out.println("PhD Grade: Fail");
    }
}

public class CollegeEvaluationApp {
    public static void main(String[] args) {
        Evaluation eval1 = new BTechEvaluation(new AttendanceModeration());
        eval1.evaluate();

        Evaluation eval2 = new MCAEvaluation(new DifficultyModeration());
        eval2.evaluate();

        Evaluation eval3 = new PhDEvaluation(new ManualModeration());
        eval3.evaluate();
    }
}
