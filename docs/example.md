# Example

For demo purposes, let's define a very simple security rating for an open-source project.
The rating is going to contain 4 features and 2 scores.

## Features

First, let's define a set of features `F = { f[1], f[2], f[3], f[4] }`:

1.  `f[1]` tells if a security review has been done for a project. The value can be `yes` or `no` (boolean).
    `Yes` means that at least one security review has been done, `no` means that no security review has ever been done.
1.  `f[2]` tells if static code analysis has been done for a project. The value can be `yes` or `no` (boolean).
     `Yes` means that static code analysis has been done, and all findings have been analyzed.
     `No` means that no static code analysis has ever been done.
1.  `f[3]` is a number of commits last month (a non-negative integer).
1.  `f[4]` is a number of contributors last month (a non-negative integer).

## Scores

Next, let's define a set of scores `S = { s[1], s[2], s[3] }`:

1.  `s[1]` is a security testing score which tells how well security testing is being done for a project.
1.  `s[2]` is a project activity score which tells how a project is active.
1.  `s[3]` is an overall security score which tells how good project security is.

Then, let's define which features the scores use:

1.  The score `s[1]` uses the features `f[1]` and `f[2]`, in other words `F_s[1] = { f[1], f[2] }`.
1.  The score `s[2]` uses the features `f[3]` and `f[4]`, in other words `F_s[2] = { f[3], f[4] }`.
1.  The score `s[3]` uses the scores `s[1]` and `s[2]`, in other words `F_s[3] = { s[1], s[2] }`.

## Score functions

Now it's time to describe the score functions.

For the security testing score, let's define a score function as the following:

```
score[1] = SecurityTestingScore

def SecurityTestingScoreExample(security_review, static_code_analysis) {
    score = 0
    if security_review is yes then
        score = score + 5
    if static_code_analysis is yes then
        score = score + 5
    return score
}
```

For the project activity score, let's define a score function as the following:

```
score[2] = ProjectActivityScore

def ProjectActivityScoreExample(commits_last_month, contributors_last_month) {
    score = 0
    if commits_last_month in (0, 10] then
        score = 2
    if commits_last_month in (10, 30] then
        score = 3
    if commits_last_month is more than 30 then
        score = 5
    if contributors_last_month in (0, 1] then
        score = score + 2
    if contributors_last_month in (1, 5] then
        score = score + 3
    if contributors_last_month is more than 5 then
        score = score + 5
    return score
}
```

Next, let's define weights for the scores. Since we're defining a security rating,
then the security testing score may be more important than the project activity score.
To address it in the rating, let's the weight for security testing score as `w[1] = 0.7`,
and the weight for project activity score as `w[2] = 0.3`.

Then, for the overall security score, let's define a score function as the following:

```
rating = score[3] = SecurityTestingScore

def SecurityTestingScoreExample(security_testing_score, project_activity_score) {
  return (w[1] * security_testing_score + w[2] * project_activity_score) / (w[1] + w[2])
}
```

## Labels

Then, let's define the set of labels `L` which describes the security score:

```
L = { Awful, Okay, Awesome }
```

## Rating

Finally, let's define a function which maps a security score value to one of the labels:

```
label = SecurityRatingExample

def SecurityRatingExample(r) {
    if r in [0, 3) then
        return Awful
    if r in [3, 8) then
        return Okay
    if r in [8, 10] then
        return Awesome
}
```

## Implementation

The implementation of the rating above can be found here:

*  [com.sap.sgs.phosphor.fosstars.model.feature.example](../src/main/java/com/sap/sgs/phosphor/fosstars/model/feature/example)
*  [com.sap.sgs.phosphor.fosstars.model.score.example](../src/main/java/com/sap/sgs/phosphor/fosstars/model/score/example)
*  [SecurityRatingExample](../src/main/java/com/sap/sgs/phosphor/fosstars/model/rating/example/SecurityRatingExample.java)

---

Next: [Quality assurance](qa.md)
