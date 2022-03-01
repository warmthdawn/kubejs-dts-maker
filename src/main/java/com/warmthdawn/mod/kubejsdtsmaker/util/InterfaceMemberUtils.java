package com.warmthdawn.mod.kubejsdtsmaker.util;

import com.warmthdawn.mod.kubejsdtsmaker.typescript.member.Member;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class InterfaceMemberUtils {


    public static boolean removeMember(List<Member> members, String name) {
        return computeMember(members, name, it -> null);
    }

    public static boolean computeMember(List<Member> members, String name, Function<Member, Member> func) {
        Iterator<Member> iterator = members.iterator();
        while (iterator.hasNext()) {
            Member member = iterator.next();
            if (name.equals(member.getName())) {
                Member apply = func.apply(member);
                if (apply != member) {
                    iterator.remove();
                    if (apply != null) {
                        members.add(apply);
                    }
                }
                return true;
            }
        }
        Member apply = func.apply(null);
        if (apply != null) {
            members.add(apply);
        }
        return false;
    }
}
