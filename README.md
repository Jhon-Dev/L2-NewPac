# L2-NewPac

             IMPORTANT CUSTOMER INFORMATION    BY: Jhonatan Nuss  
                     http://JnCenter.com .br                      
                    Project based on Acis 390                     

#--MODS--#


#--COMMANDS--#
.Online
.Stats

#--HANDLERS--#
<set name="handler" val="ClanFull" />
<set name="handler" val="DeletePk" />




#--EVENTS--#


#--RESTRITIONS--#

1 - RESTRIÇÃO DE ITENS POR CLASSE 

Você precisa adicionar o ID da classe e separar (,) cada classe, como no exemplo abaixo.

<item id="9222" type="Armor" name="Titanium Heavy Armor">
        <set name="icon" val="icon.armor_t1004_ul_i00" />
        <set name="default_action" val="equip" />
        <set name="armor_type" val="HEAVY" />
        <set name="bodypart" val="fullarmor" />
        <set name="crystal_type" val="S" />
        <set name="crystal_count" val="870" />
        <set name="material" val="LEATHER" />
        <set name="weight" val="4950" />
        <set name="price" val="1740000" />
        <cond msgId="1518">
               <and>
                <player classId="91,90,89" />
               </and>
        </cond>
        <for>
            <add order="0x10" stat="pDef" val="393" />
            <enchant order="0x0C" stat="pDef" val="0" />
        </for>
    </item>
