import React, { useState } from "react";
import { Link } from "react-router-dom";
// nodejs library that concatenates classes
import classNames from "classnames";
// @material-ui/core components
import { makeStyles } from "@material-ui/core/styles";
// @material-ui/icons

//import Palette from "@material-ui/icons/Palette";
//import Button from "components/CustomButtons/Button.js";

// core components
import Header from "components/Header/Header.js";
import Footer from "components/Footer/Footer.js";
import GridContainer from "components/Grid/GridContainer.js";
import GridItem from "components/Grid/GridItem.js";
import Parallax from "components/Parallax/Parallax.js";
import Button from "components/CustomButtons/Button.js";
import Paper from "@material-ui/core/Paper";
import Grid from "@material-ui/core/Grid";
import styles from "assets/jss/material-kit-react/views/articlePage.js";
// import { getProfileInfo } from "../../service/profileinformation.service";
import { getPublicArticles } from "service/article.service.js";
import PheaderLinks from "components/ProfileHeader/PheaderLinks";
import Icon from "@material-ui/core/Icon";
import Typography from "@material-ui/core/Typography";
import ButtonBase from "@material-ui/core/ButtonBase";

import articleThumbnail from "assets/img/examples/investor.jpeg";

const useStyles = makeStyles(styles);

export default function ArticlesPage(props) {
  const classes = useStyles();
  const { ...rest } = props;
  // const imageClasses = classNames(
  //   classes.imgRaised,
  //   classes.imgRoundedCircle,
  //   classes.imgFluid
  // );
  const [values, setValues] = useState({
    count: 0,
    results: []
  });

  useState(() =>
    getPublicArticles().then(res =>
      setValues({ count: res.count, results: res.results })
    )
  );
  const items = [];
  for (const [index, value] of values.results.entries()) {
    items.push(
      <Paper className={classes.paper}>
        <Grid container spacing={2}>
          <Grid item>
            <ButtonBase className={classes.image}>
              <img
                className={classes.img}
                alt="complex"
                src={articleThumbnail}
              />
            </ButtonBase>
          </Grid>
          <Grid item xs={120} sm container>
            <Grid item xs container direction="column" spacing={2}>
              <Grid item xs>
                <Typography gutterBottom variant="subtitle1">
                  {value.title}
                </Typography>
                <Typography variant="body2" gutterBottom>
                  {value.content}
                </Typography>
              </Grid>
              <Grid item>
                <Typography variant="body2" style={{ cursor: "pointer" }}>
                  Author: {value.author.first_name + " " + value.author.last_name}
                </Typography>
                <div style={{ float: "right" }}>
                  <Link
                    to={{
                      pathname: "/article/" + value.id,
                      state: { id: value.id }
                    }}
                  >
                    <Button variant="contained" color="secondary">
                      Read more
                    </Button>
                  </Link>
                </div>
              </Grid>
            </Grid>
          </Grid>
        </Grid>
      </Paper>
      // <li key={index}>{value.title}</li>
    );
  }

  // const navImageClasses = classNames(classes.imgRounded, classes.imgGallery);
  return (
    <div>
      <Header
        color="transparent"
        brand="Khaji-it Traders Platform"
        rightLinks={<PheaderLinks />}
        fixed
        changeColorOnScroll={{
          height: 200,
          color: "white"
        }}
        {...rest}
      />
      <Parallax small filter image={require("assets/img/dollar-hd.jpg")} />
      <div className={classNames(classes.main, classes.mainRaised)}>
        <div>
          <div className={classes.container}>
            <GridContainer justify="center">
              <GridItem xs={12} sm={12} md={20}>
                <div className={classes.root}>{items}</div>
              </GridItem>
              <div style={{ float: "right" }}>
                <Link to="/add-article">
                  <Button
                    variant="contained"
                    color="primary"
                    className={classes.button}
                    startIcon={<Icon>add_circle_outline</Icon>}
                  >
                    Add Article
                  </Button>
                </Link>
              </div>
            </GridContainer>
            <GridContainer justify="center"></GridContainer>
          </div>
        </div>
      </div>
      <Footer />
    </div>
  );
}
