:: This script will open a tunnel to the default TwilioCallRouter port using https://github.com/TrueWinter/localtunnel and give you an address like https://abcdefgjhij.localtunnel.me. This allows Twilio to connect directly to TwilioCallRouter, even if you're behind NAT.
:: If possible, it will provide you with the same tunnel URL every time you start the tunnel so you don't need to constantly update the URL in the Twilio console.
:: Please install the TrueWinter fork of localtunnel using the instructions in the GitHub link above.

@echo off

Set _tunnelId=%RANDOM%%RANDOM%

if exist ltwTunId.txt (
	Set /p _tunnelId=<ltwTunId.txt
) else (
	echo %_tunnelId%>ltwTunId.txt
)

ltw -p 8500 --print-requests -s twiliocallrouter-%_tunnelId%