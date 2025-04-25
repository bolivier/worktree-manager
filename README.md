# Worktree Manager

Makes using worktrees easier

At the core, it just creates a new worktree with a branch name and installs deps.

Use --help to learn more.

## Installation

Install `babashka`

Either from Homebrew:

``` shell
brew install borkdude/brew/babashka
```

or straight through the shell.

``` shell
bash < <(curl -s https://raw.githubusercontent.com/babashka/babashka/master/install)
```

Put `<workspace-root>/bin/wtm` on your path. There's a task you can run with `bb
install` that will stick it in `~/bin`.

### Optional:

Install autocomplete so you don't have to type branch names or worktree paths to remove.

#### Fish
Symlink the autocomplete file `<workspace-root>/resources/autocomplete/wtm.fish` to `~/.config/fish/completions/wtm.fish`.

#### Zsh
Add a line in your `.zshrc` to source the file `<workspace-root>/resources/autocomplete/wtm-completion.zsh` 

#### Bash
Add a line in your `.bashrc` to source the file `<workspace-root>/resources/autocomplete/wtm-completion.bash`
