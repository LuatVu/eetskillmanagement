package com.bosch.eet.skill.management.facade.util;

import java.text.ParseException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import com.bosch.eet.skill.management.common.Constants;
import com.bosch.eet.skill.management.exception.BadRequestException;
import com.bosch.eet.skill.management.exception.MessageCode;

public final class Utility {

    @Autowired
    public static MessageSource messageSource;

    static public Date parseSimpleDateFormat(String date) {
        try {
            return Constants.SIMPLE_DATE_FORMAT.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new BadRequestException(messageSource.getMessage(MessageCode.WRONG_DATE_FORMAT.toString(), null,
                    LocaleContextHolder.getLocale()));
        }
    }

    static public float validateSkillLevel(String levelStr) {
        float level = parseFloat(levelStr);
        if(!Constants.EXPECTED_SKILL_LEVELS.contains(level)){
            throw new BadRequestException(messageSource.getMessage(MessageCode.SKILL_LEVEL_INVALID.toString(), null,
                    LocaleContextHolder.getLocale()));
        }
        return level;
    }

    static public float parseFloat(String levelStr) {
        try {
            return Float.parseFloat(levelStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw new BadRequestException(messageSource.getMessage(MessageCode.SKILL_LEVEL_INVALID.toString(), null,
                    LocaleContextHolder.getLocale()));
        }
    }

    static public String floatToStringLevelFormat(float level){
        if(level == (long) level)
            return String.format("%d",(long)level);
        else
            return String.format("%s",level);
    }


}
