#!/bin/sh
cd ..
git config –add checkstyle.jar config/checkstyle-7.0-all.jar
git config –add checkstyle.checkfile config/checks.xml
cp config/pre-commit ./.git/hooks/