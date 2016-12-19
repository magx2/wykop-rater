package pl.grzeslowski.wykop.posts;

import com.google.common.base.Preconditions;

public final class Score {
    private final int upVotes;
    private final int downVotes;

    public Score(int upVotes, int downVotes) {
        Preconditions.checkArgument(upVotes >= 0, "upVotes == " + upVotes);
        Preconditions.checkArgument(downVotes >= 0, "downVotes == " + downVotes);
        this.upVotes = upVotes;
        this.downVotes = downVotes;
    }

    public int getUpVotes() {
        return upVotes;
    }

    public int getDownVotes() {
        return downVotes;
    }

    public int calculateScore() {
        return upVotes - downVotes;
    }

    public boolean isPositive() {
        return calculateScore() > 0;
    }

    public boolean isNegative() {
        return calculateScore() < 0;
    }

    public boolean isNeutral() {
        return calculateScore() == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Score)) return false;

        Score score = (Score) o;

        return upVotes == score.upVotes && downVotes == score.downVotes;
    }

    @Override
    public int hashCode() {
        int result = upVotes;
        result = 31 * result + downVotes;
        return result;
    }

    @Override
    public String toString() {
        return "Score{" +
                "upVotes=" + upVotes +
                ", downVotes=" + downVotes +
                '}';
    }
}
