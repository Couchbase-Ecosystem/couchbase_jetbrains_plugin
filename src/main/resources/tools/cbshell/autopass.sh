#!/usr/bin/expect -f

# Configuration
set timeout -1

# Capture the command from the first argument and the password from the second
set command [lindex $argv 0]
set password [lindex $argv 1]

# Launch the process
eval spawn $command

# Wait for password prompt, then send password
expect "Password:" { send "$password\r" }

# Wait for creation question, then send "n"
expect "Would you like to create one now (Y/n)?" { send "n\r" }

sleep 1

send "clear\r"
send "buckets\r"

# Hand over control to the user
interact