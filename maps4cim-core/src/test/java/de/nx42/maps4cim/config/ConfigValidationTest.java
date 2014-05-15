package de.nx42.maps4cim.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;

import org.junit.Test;

import de.nx42.maps4cim.config.bounds.BBoxDef;
import de.nx42.maps4cim.config.bounds.CenterDef;
import de.nx42.maps4cim.config.relief.HeightmapDef;
import de.nx42.maps4cim.config.relief.SrtmDef;
import de.nx42.maps4cim.config.texture.ColorDef;
import de.nx42.maps4cim.config.texture.ImageDef;
import de.nx42.maps4cim.config.texture.OsmDef;
import de.nx42.maps4cim.config.texture.osm.NodeDef;
import de.nx42.maps4cim.config.texture.osm.WayDef;
import de.nx42.maps4cim.util.ValidatorUtils;


public class ConfigValidationTest {
    
    @Test
    public void ovalTestBounds() {
        Config c = ConfigTest.generateConfig();
        c.bounds = null;
        
        List<ConstraintViolation> cvs = ValidatorUtils.validateR(c);
        assertEquals(1, cvs.size());
    }

    @Test
    public void ovalTestBounds2() {
        Config c = ConfigTest.generateConfig();
        c.bounds.value = BBoxDef.of(48, 11, 49, 12);

        List<ConstraintViolation> cvs = ValidatorUtils.validateR(c);
        assertEquals(0, cvs.size());
    }

    @Test
    public void ovalTestCenterDef() {
        Config c = ConfigTest.generateConfig();
        CenterDef cd = (CenterDef) c.getBoundsTrans();
        cd.centerLat = null;
        
        List<ConstraintViolation> cvs = ValidatorUtils.validateR(c);
        assertEquals(1, cvs.size());
        assertNull(cvs.get(0).getInvalidValue());
    }
    
    @Test
    public void ovalTestCenterDef2() {
        Config c = ConfigTest.generateConfig();
        CenterDef cd = (CenterDef) c.getBoundsTrans();
        cd.centerLon = 181.0;
        
        List<ConstraintViolation> cvs = ValidatorUtils.validateR(c);
        assertEquals(1, cvs.size());
        assertEquals(181.0, cvs.get(0).getInvalidValue());
    }

    @Test
    public void ovalTestCenterDef3() {
        Config c = ConfigTest.generateConfig();
        CenterDef cd = (CenterDef) c.getBoundsTrans();
        cd.extent = null;
        cd.extentLat = 8.0;
        
        List<ConstraintViolation> cvs = ValidatorUtils.validateR(c);
        assertEquals(1, cvs.size());
    }

    @Test
    public void ovalTestCenterDef4() {
        Config c = ConfigTest.generateConfig();
        CenterDef cd = (CenterDef) c.getBoundsTrans();
        cd.extent = null;
        cd.extentLat = 8.0;
        cd.extentLon = 12.0;
        
        List<ConstraintViolation> cvs = ValidatorUtils.validateR(c);
        assertEquals(0, cvs.size());
    }
    
    @Test
    public void ovalTestBBoxDef() {
        Config c = ConfigTest.generateConfig();
        c.bounds.value = BBoxDef.of(48, 11, 49, 12);
        BBoxDef bb = (BBoxDef) c.getBoundsTrans();
        bb.maxLat = null;
        
        List<ConstraintViolation> cvs = ValidatorUtils.validateR(c);
        assertTrue(cvs.size() > 0);
    }
    

    @Test
    public void ovalTestBBoxDef2() {
        Config c = ConfigTest.generateConfig();
        c.bounds.value = BBoxDef.of(48, 11, 49, 12);
        BBoxDef bb = (BBoxDef) c.getBoundsTrans();
        bb.minLat = 48.0;
        bb.maxLat = 47.9;
        
        List<ConstraintViolation> cvs = ValidatorUtils.validateR(c);
        assertTrue(cvs.size() > 0);
    }
    
    @Test
    public void ovalTestHeightMapDef() {
        Config c = ConfigTest.generateConfig();
        c.relief.value = HeightmapDef.of("~/heightmap.png", -100.0, 300.0);
        
        List<ConstraintViolation> cvs = ValidatorUtils.validateR(c);
        assertTrue(cvs.size() == 0);
    }
    
    @Test
    public void ovalTestHeightMapDef2() {
        Config c = ConfigTest.generateConfig();
        c.relief.value = HeightmapDef.of(null, -100.0, 300.0);
        
        List<ConstraintViolation> cvs = ValidatorUtils.validateR(c);
        assertTrue(cvs.size() > 0);
    }
    
    @Test
    public void ovalTestHeightMapDef3() {
        Config c = ConfigTest.generateConfig();
        c.relief.value = HeightmapDef.of("~/heightmap.png", 100.0, 50.0);
        
        List<ConstraintViolation> cvs = ValidatorUtils.validateR(c);
        assertTrue(cvs.size() > 0);
    }
    
    @Test
    public void ovalTestHeightMapDef4() {
        Config c = ConfigTest.generateConfig();
        c.relief.value = HeightmapDef.of("~/heightmap.png", 100.0, null);
        
        List<ConstraintViolation> cvs = ValidatorUtils.validateR(c);
        assertTrue(cvs.size() > 0);
    }

    @Test
    public void ovalTestSrtmDef() {
        Config c = ConfigTest.generateConfig();
        
        c.relief.value = SrtmDef.of("auto", "1.0");
        assertTrue(ValidatorUtils.validateR(c).size() == 0);
        
        c.relief.value = SrtmDef.of("auto", null);
        assertTrue(ValidatorUtils.validateR(c).size() == 0);
        
        c.relief.value = SrtmDef.of("42", "0.75");
        assertTrue(ValidatorUtils.validateR(c).size() == 0);
        
        c.relief.value = SrtmDef.of("-42", "-0.75");
        assertTrue(ValidatorUtils.validateR(c).size() == 0);

        c.relief.value = SrtmDef.of("  auto ", " 1.0   ");
        assertTrue(ValidatorUtils.validateR(c).size() == 0);
        
        c.relief = null;
        assertTrue(ValidatorUtils.validateR(c).size() == 0);
    }
    
    @Test
    public void ovalTestSrtmDef2() {
        Config c = ConfigTest.generateConfig();
        
        c.relief.value = SrtmDef.of("autoauto", "1.0");
        assertTrue(ValidatorUtils.validateR(c).size() > 0);
        
        c.relief.value = SrtmDef.of("nonne", null);
        assertTrue(ValidatorUtils.validateR(c).size() > 0);
        
        c.relief.value = SrtmDef.of("42.", null);
        assertTrue(ValidatorUtils.validateR(c).size() > 0);

        c.relief.value = SrtmDef.of(null, ".75");
        assertTrue(ValidatorUtils.validateR(c).size() > 0);
        
        c.relief.value = SrtmDef.of("+42", "+0.75");
        assertTrue(ValidatorUtils.validateR(c).size() > 0);
    }

    @Test
    public void ovalTestColorDef() {
        ColorDef c = new ColorDef() {{ name="wood"; roughGrass=0.4; }};
        assertTrue(ValidatorUtils.validateR(c).size() == 0);
        
        c = new ColorDef() {{ roughGrass=-0.4; }};
        assertTrue(ValidatorUtils.validateR(c).size() > 0);
        
        c = new ColorDef() {{ roughGrass=1.1; }};
        assertTrue(ValidatorUtils.validateR(c).size() > 0);
    }

    @Test
    public void ovalTestImageDef() {
        Config c = ConfigTest.generateConfig();
        ImageDef i = new ImageDef();
        i.imageFilePath = "~/image.png";
        i.fillMissingColors();
        c.texture.value = i;
        
        assertTrue(ValidatorUtils.validateR(c).size() == 0);
        
        i.blueTranslation = new ColorDef() {{ roughGrass=-0.4; }};
        assertTrue(ValidatorUtils.validateR(c).size() > 0);
        
        i.imageFilePath = "  ";
        assertTrue(ValidatorUtils.validateR(c).size() > 0);
        
        i.imageFilePath = null;
        assertTrue(ValidatorUtils.validateR(c).size() > 0);
    }

    @Test
    public void ovalTestOsmDef() {
        Config c = ConfigTest.generateConfig();
        OsmDef osm = (OsmDef) c.getTextureTrans();
        assertTrue(ValidatorUtils.validateR(c).size() == 0);

        // color
        ColorDef color = osm.colors.iterator().next();
        color.black = 2.0;
        assertTrue(ValidatorUtils.validateR(c).size() > 0);
        color.black = 0.0;
        
        // node
        NodeDef node = new NodeDef();
        osm.entities.add(node);
        assertTrue(ValidatorUtils.validateR(c).size() > 0);
        
        node.key = "  ";
        node.color = "";
        assertTrue(ValidatorUtils.validateR(c).size() > 0);
        
        node.key = "barrier";
        node.color = "red";
        assertTrue(ValidatorUtils.validateR(c).size() == 0);
        
        node.radius = -1.0;
        assertTrue(ValidatorUtils.validateR(c).size() > 0);
        
        node.radius = 5.5;
        assertTrue(ValidatorUtils.validateR(c).size() == 0);
        
        // way
        WayDef way = new WayDef();
        way.key = "highway";
        way.color = "black";
        osm.entities.add(way);
        assertTrue(ValidatorUtils.validateR(c).size() == 0);
        
        way.strokeWidth = -1.0;
        assertTrue(ValidatorUtils.validateR(c).size() > 0);
        
    }
    
    
    
    protected static void validateAndPrint(Object o) {
        Validator validator = new Validator();
        List<ConstraintViolation> violations = validator.validate(o);
        System.out.println(ValidatorUtils.formatCausesRecursively(violations));
    }
    
}
