package org.orcid.jaxb.model.common;

public enum LanguageCode {
    ab, aa, af, ak, sq, am, ar, an, hy, as, av, ae, ay, az, bm, ba, eu, be, bn, bh, bi, bs, br, bg, my, ca, ch, ce, zh_CN, zh_TW, cu, cv, kw, co, cr, hr, cs, da, dv, nl, dz, en, eo, et, ee, fo, fj, fi, fr, fy, ff, gl, lg, ka, de, el, kl, gn, gu, ht, ha, iw, hz, hi, ho, hu, is, io, ig, in, ia, ie, iu, ik, ga, it, ja, jv, kn, kr, ks, kk, km, ki, rw, ky, kv, kg, ko, ku, kj, lo, la, lv, li, ln, lt, lu, lb, mk, mg, ms, ml, mt, gv, mi, mr, mh, mo, mn, na, nv, ng, ne, nd, se, no, nb, nn, ny, oc, oj, or, om, os, pi, pa, fa, pl, pt, ps, qu, rm, ro, rn, ru, sm, sg, sa, sc, gd, sr, sn, ii, sd, si, sk, sl, so, nr, st, es, su, sw, ss, sv, tl, ty, tg, ta, tt, te, th, bo, ti, to, ts, tn, tr, tk, tw, ug, uk, ur, uz, ve, vi, vo, wa, cy, wo, xh, ji, yo, za, zu;

    public static String[] getValues() {
        LanguageCode[] codes = LanguageCode.values();
        String[] values = new String[codes.length];
        for (int i = 0; i < codes.length; i++) {
            values[i] = codes[i].name();
        }
        return values;
    }
}
