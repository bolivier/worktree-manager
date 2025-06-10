function wtm() {
  if [[ "$1" == "switch" ]]; then
    # Run `wtm list` and store lines in an array
    local options selected
    IFS=$'\n' options=($(wtm list))

    # Use fzf to select one of the options
    selected=$(printf "%s\n" "${options[@]}" | fzf)

    # If the user made a selection, cd into it
    if [[ -n "$selected" ]]; then
      cd "$selected"
    fi
  else
    command wtm "$@"
  fi
}
