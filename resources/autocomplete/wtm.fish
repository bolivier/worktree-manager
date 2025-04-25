function __wtm_complete
  set -l tokens (commandline -opc)
  wtm _complete $tokens
end

complete -c wtm -f -a '(__wtm_complete)'
