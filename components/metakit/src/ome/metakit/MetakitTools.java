//
// MetakitTools.java
//

/*
OME Metakit package for reading Metakit database files.
Copyright (C) 2011-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package ome.metakit;

import java.io.IOException;
import java.util.ArrayList;

import loci.common.RandomAccessInputStream;

/**
 * Utility methods for working with Metakit database files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/metakit/src/ome/metakit/MetakitTools.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/metakit/src/ome/metakit/MetakitTools.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class MetakitTools {

  /**
   * Read a Pascal-style string from the given RandomAccessInputStream.
   */
  public static String readPString(RandomAccessInputStream stream)
    throws IOException
  {
    int stringLength = readBpInt(stream);
    return stream.readString(stringLength);
  }

  /**
   * Read a byte-packed integer from the given RandomAccessInputStream.
   *
   * See http://equi4.com/metakit/metakit-ff.html#formatgrammar for a
   * description of byte-packed integers.
   */
  public static int readBpInt(RandomAccessInputStream stream) throws IOException
  {
    int signByte = stream.read();
    boolean negative = signByte == 0;
    int dataByte = !negative ? signByte : stream.read();
    int stopByte = dataByte;
    ArrayList<Integer> dataBytes = new ArrayList<Integer>();
    while ((stopByte & 0x80) == 0) {
      dataBytes.add(stopByte);
      stopByte = stream.read();
    }

    int value = 0;
    for (int i=0; i<dataBytes.size(); i++) {
      int shift = (dataBytes.size() - i) * 7;
      value |= (dataBytes.get(i) << shift);
    }

    value |= (stopByte & 0x7f);

    if (negative) {
      value = ~value;
    }
    return value;
  }

}
