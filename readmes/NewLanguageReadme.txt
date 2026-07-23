Do the following steps in order to add a new language:
1 - Edit the following file nano.server.utils.LocalizerUtils and add the new language name to the AVAILABLE_LOCALES variable.
2 - Add the localization file to /src/main/resources/localization/dictionary_[new language name].properties.
3 - Add the image file for the flag in /src/main/webapp/resources/img/[new language name].svg.
Note: For example, the language name should be like "pt" for Portuguese.