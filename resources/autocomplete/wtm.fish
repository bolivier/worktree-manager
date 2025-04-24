function __wtm_complete
  wtm _complete (commandline -opc)
end

complete -c wtm -a "(__wtm_complete)"
