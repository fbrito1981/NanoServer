Do the following steps in order to create a new section:
1 - Update the table creation script for sections: /scripts/tbls/sections.sql.
2 - Run the individual script to add the new section in the DB.
3 - Create a new entry at the localization files named like: menu_item_[new section name]=New section name.
4 - Edit the method getIcon() in nano.server.dtos.MenuItemDto in order to add the icon for the menu option.
5 - Create the new controller and jsp file accordingly.