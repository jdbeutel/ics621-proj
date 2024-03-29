/* -*- tab-width: 4 -*-
 *
 * Electric(tm) VLSI Design System
 *
 * File: UnboxedFunction.java
 *
 * Copyright (c) 2009 Sun Microsystems and Static Free Software
 *
 * Electric(tm) is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * Electric(tm) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Electric(tm); see the file COPYING.  If not, write to
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, Mass 02111-1307, USA.
 */
package btree.com.sun.electric.database.geometry.btree.unboxed;

import java.io.*;

/**
 *  A function from one Unboxed to another.
 */
public interface UnboxedFunction<A extends Serializable, B extends Serializable> {

    /**
     *  The function <b>MUST</b> support situations where the argument
     *  and return buffers overlap.
     */
    public void call(byte[] buf_a, int ofs_a,
                     byte[] buf_b, int ofs_b);

}