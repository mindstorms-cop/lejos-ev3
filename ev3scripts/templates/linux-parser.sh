#!/bin/bash

# set classpath to . by default (matches behaviour of java and javac)
EV3_CMDLINE_CP="$EV3_CP_PC$SEP."
for (( i=1; i<=$#; i++ )); do
	case "${!i}" in
		#handle classpath parameters
		-cp|-classpath)
			(( i++ ))
			EV3_CMDLINE_CP="$EV3_CP_PC$SEP${!i}"
			;;
		#handle other parameters that accept arguments
		-sourcepath|-bootclasspath|-extdirs|-endorseddirs|-processor|-processorpath|-d|-s|-encoding|-source|-target|-Xmaxerrs|-Xmaxwarns|-Xstdout)
			EV3_CMDLINE[$i]="${!i}"
			(( i++ ))
			EV3_CMDLINE[$i]="${!i}"
			;;
		#abort parsing at -jar or classname
		-jar|[!-]*)
			for (( ; i<=$#; i++ )); do
				EV3_CMDLINE[$i]="${!i}"
			done
			;;
		#handle parameters without arguments
		*)
			EV3_CMDLINE[$i]="${!i}"
	esac
done
