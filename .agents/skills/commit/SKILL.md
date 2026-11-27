---
name: commit
description: Commit the current repository changes when the user explicitly invokes this skill. Group distinct tasks into separate commits with clear messages.
---

# Commit current work

Use this skill only when explicitly invoked. The invocation authorizes committing the changes already in scope; it does not authorize pushing unless the user also asks to push.

1. Inspect the branch, `git status`, staged and unstaged diffs, and relevant untracked files. Read repository instructions that govern commits. Identify which changes belong to each completed task, including work left uncommitted from earlier turns.
2. Verify each task enough to make its commit honest. Report a failed check and fix the task if feasible. Keep unrelated or unfinished changes out of that commit; preserve any user changes you do not understand.
3. Make one commit per coherent task. Stage explicit paths or hunks, inspect the staged diff, then use `git commit -m "<specific message>"`. Make the message say what changed, using the repository's existing style when one is visible. Do not combine separate tasks merely to reduce commit count.
4. After all intended commits, report their hashes, messages, any changes still uncommitted, and verification results. If the user also requested a push, push the intended branch and verify its status against the remote.

Never stage secrets, local machine configuration, generated build output, or files excluded by repository rules. Avoid rewriting existing commits unless the user explicitly requests it. If a change cannot be assigned safely to a task, explain what needs clarification while committing independent, clearly scoped work.
